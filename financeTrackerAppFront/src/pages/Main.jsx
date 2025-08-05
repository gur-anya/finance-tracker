import { useState, useEffect } from "react";
import TransactionCard from "../components/TransactionCard.jsx";
import AddTransactionCard from "../components/AddTransactionCard.jsx";
import MainTabs from "../components/MainTabs.jsx";
import CreateTransactionModal from "../components/CreateTransactionModal.jsx";
import Stats from "../components/Stats.jsx";
import Goals from "../components/Goals.jsx"; // Added Goals import
import { Button, Alert, Spinner } from 'react-bootstrap';
import Dropdown from 'react-bootstrap/Dropdown';
import { DropdownButton } from "react-bootstrap";
import apiService from '../services/api';

function Main() {
    const [transactions, setTransactions] = useState([]);
    const [filteredTransactions, setFilteredTransactions] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showCreateModal, setShowCreateModal] = useState(false);
    const [filters, setFilters] = useState({
        type: 'all',
        category: 'all'
    });
    const [refreshKey, setRefreshKey] = useState(0); // Ключ для принудительного обновления

    const fetchTransactions = async () => {
        setIsLoading(true);
        setError(null);
        
        try {
            const response = await apiService.getAllTransactions();
            // Теперь API возвращает массив транзакций напрямую
            setTransactions(response || []);
            setFilteredTransactions(response || []);
        } catch (error) {
            setError('Ошибка загрузки транзакций: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    // Функция для принудительного обновления всех компонентов
    const refreshAllData = () => {
        setRefreshKey(prev => prev + 1);
    };

    const handleTransactionCreated = async (newTransaction) => {
        try {
            const createdTransaction = await apiService.createTransaction(newTransaction);
            // Обновляем список транзакций
            await fetchTransactions();
            // Принудительно обновляем статистику и цели
            refreshAllData();
        } catch (error) {
            setError('Ошибка создания транзакции: ' + error.message);
        }
    };

    const handleDeleteTransaction = async (transactionId) => {
        try {
            await apiService.deleteTransaction(transactionId);
            // Обновляем список транзакций
            await fetchTransactions();
            // Принудительно обновляем статистику и цели
            refreshAllData();
        } catch (error) {
            setError('Ошибка удаления транзакции: ' + error.message);
        }
    };

    const handleEditTransaction = async (transactionId, updatedData) => {
        try {
            await apiService.updateTransaction(transactionId, updatedData);
            // Обновляем список транзакций
            await fetchTransactions();
            // Принудительно обновляем статистику и цели
            refreshAllData();
        } catch (error) {
            setError('Ошибка обновления транзакции: ' + error.message);
        }
    };

    const applyFilters = async () => {
        if (filters.type === 'all' && filters.category === 'all') {
            // Если фильтры не применены, показываем все транзакции
            setFilteredTransactions(transactions);
            return;
        }

        try {
            // Используем фейковый эндпоинт для фильтрации
            const response = await apiService.filterTransactions(filters);
            // Теперь API возвращает массив транзакций напрямую
            setFilteredTransactions(response || []);
        } catch (error) {
            console.error('Ошибка фильтрации:', error);
            // В случае ошибки применяем фильтры локально
            let filtered = [...transactions];
            
            if (filters.type !== 'all') {
                filtered = filtered.filter(t => t.type.toString() === filters.type);
            }
            
            if (filters.category !== 'all') {
                filtered = filtered.filter(t => t.category === filters.category);
            }
            
            setFilteredTransactions(filtered);
        }
    };

    // Новый обработчик для динамического удаления транзакций цели
    const handleGoalTransactionsCleared = () => {
        setTransactions(prev => prev.filter(t => t.category !== 'ЦЕЛЬ'));
        setFilteredTransactions(prev => prev.filter(t => t.category !== 'ЦЕЛЬ'));
    };

    useEffect(() => {
        fetchTransactions();
    }, []);

    useEffect(() => {
        applyFilters();
    }, [filters, transactions]);

    const statsContent = <Stats key={`stats-${refreshKey}`} onStatsChange={refreshAllData} />;
    const goalsContent = <Goals key={`goals-${refreshKey}`} onGoalChange={refreshAllData} onGoalTransactionsCleared={handleGoalTransactionsCleared} />;
    const transactionsContent = (
        <TransactionsList 
            transactions={filteredTransactions}
            isLoading={isLoading}
            error={error}
            onDelete={handleDeleteTransaction}
            onEdit={handleEditTransaction}
            onTransactionCreated={handleTransactionCreated}
        />
    );

    return (
        <div className="tabs-main-container">
            <MainTabs 
                transactionsContent={transactionsContent} 
                statsContent={statsContent}
                goalsContent={goalsContent}
            />
            
            <CreateTransactionModal 
                show={showCreateModal}
                onHide={() => setShowCreateModal(false)}
                onTransactionCreated={handleTransactionCreated}
            />
        </div>
    );
}

function TransactionsList({ transactions, isLoading, error, onDelete, onEdit, onTransactionCreated }) {
    const [showFilters, setShowFilters] = useState(false);
    const [filters, setFilters] = useState({
        type: 'all',
        category: 'all'
    });

    const handleShowFilters = () => {
        setShowFilters(!showFilters);
    };

    const handleFilterChange = (filterType, value) => {
        setFilters(prev => ({
            ...prev,
            [filterType]: value
        }));
    };

    const resetFilters = () => {
        setFilters({
            type: 'all',
            category: 'all'
        });
    };

    const typeDisplay = filters.type === 'all' ? 'Все' : filters.type === '1' ? 'Доход' : 'Расход';
    const categoryDisplay = filters.category === 'all' ? 'Все' : filters.category;

    const categories = ['ЕДА', 'ТРАНСПОРТ', 'РАЗВЛЕЧЕНИЯ', 'ПОКУПКИ', 'ЗДОРОВЬЕ', 'ОБРАЗОВАНИЕ', 'ДОМ', 'ЗАРПЛАТА', 'ФРИЛАНС', 'ПОДАРКИ', 'ИНВЕСТИЦИИ', 'ПРОДАЖИ', 'ДРУГОЕ'];

    if (isLoading) {
        return (
            <div className="text-center p-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Загрузка...</span>
                </Spinner>
                <p className="mt-2">Загрузка транзакций...</p>
            </div>
        );
    }

    if (error) {
        return (
            <Alert variant="danger">
                {error}
            </Alert>
        );
    }

    return (
        <>
            <div className="d-flex justify-content-between align-items-center mb-3">
                <h4>Транзакции ({transactions.length})</h4>
            </div>

            <div className="filters-block mb-3">
                <Button 
                    variant="outline-secondary" 
                    onClick={handleShowFilters}
                    className="mb-2"
                >
                    {showFilters ? 'Скрыть фильтры' : 'Показать фильтры'}
                </Button>
                
                {showFilters && (
                    <div className="filters-container p-3 border rounded">
                        <div className="row">
                            <div className="col-md-4 mb-2">
                                <label className="form-label">Тип:</label>
                                <DropdownButton
                                    id="type-filter-dropdown"
                                    title={typeDisplay}
                                    onSelect={(eventKey) => handleFilterChange('type', eventKey)}
                                    variant="outline-secondary"
                                    className="w-100"
                                >
                                    <Dropdown.Item eventKey="all">Все</Dropdown.Item>
                                    <Dropdown.Item eventKey="1">Доход</Dropdown.Item>
                                    <Dropdown.Item eventKey="0">Расход</Dropdown.Item>
                                </DropdownButton>
                            </div>
                            
                            <div className="col-md-4 mb-2">
                                <label className="form-label">Категория:</label>
                                <DropdownButton
                                    id="category-filter-dropdown"
                                    title={categoryDisplay}
                                    onSelect={(eventKey) => handleFilterChange('category', eventKey)}
                                    variant="outline-secondary"
                                    className="w-100"
                                >
                                    <Dropdown.Item eventKey="all">Все</Dropdown.Item>
                                    {categories.map(category => (
                                        <Dropdown.Item key={category} eventKey={category}>
                                            {category}
                                        </Dropdown.Item>
                                    ))}
                                </DropdownButton>
                            </div>
                            
                            <div className="col-md-4 d-flex align-items-end">
                                <Button 
                                    variant="outline-danger" 
                                    onClick={resetFilters}
                                    className="w-100"
                                >
                                    Сбросить фильтры
                                </Button>
                            </div>
                        </div>
                    </div>
                )}
            </div>

            <div className="transactions-list">
                <div className="transactions-grid">
                    {/* Карточка добавления всегда первая */}
                    <AddTransactionCard onTransactionCreated={onTransactionCreated} />
                    
                    {/* Существующие транзакции */}
                    {transactions.length === 0 ? (
                        <div className="text-center p-5">
                            <p className="text-muted">Транзакции не найдены</p>
                        </div>
                    ) : (
                        transactions.map((transaction) => {
                            // Проверяем, что у транзакции есть ID
                            if (!transaction.id) {
                                console.warn('Transaction without ID:', transaction);
                                console.warn('Transaction keys:', Object.keys(transaction));
                                console.warn('Transaction values:', Object.values(transaction));
                                return null;
                            }
                            
                            return (
                                <TransactionCard
                                    key={transaction.id}
                                    id={transaction.id}
                                    type={transaction.type}
                                    timestamp={transaction.timestamp}
                                    category={transaction.category}
                                    sum={transaction.sum}
                                    description={transaction.description}
                                    onDelete={() => onDelete(transaction.id)}
                                    onEdit={(transactionId, updatedData) => onEdit(transactionId, updatedData)}
                                />
                            );
                        }).filter(Boolean)
                    )}
                </div>
            </div>
        </>
    );
}

export default Main;