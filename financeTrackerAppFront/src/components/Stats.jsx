import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Form, Alert, Spinner, ProgressBar, Modal } from 'react-bootstrap';
import apiService from '../services/api';

function Stats({ onStatsChange }) {
    const [stats, setStats] = useState(null);
    const [balance, setBalance] = useState(null);
    const [balanceLimit, setBalanceLimit] = useState(100000); // Стартовый лимит
    const [period, setPeriod] = useState({
        startDate: new Date(new Date().getFullYear(), new Date().getMonth(), 1).toISOString().split('T')[0],
        endDate: new Date(2025, 7, 10).toISOString().split('T')[0] // 10 августа 2025
    });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showBudgetModal, setShowBudgetModal] = useState(false);
    const [budgetForm, setBudgetForm] = useState({
        limit: ''
    });

    // Функция для уведомления родительского компонента об изменениях
    const notifyParent = () => {
        if (onStatsChange) {
            onStatsChange();
        }
    };

    const fetchBalance = async () => {
        try {
            // Преобразуем даты в правильный формат для бэкенда
            const formattedPeriod = {
                startDate: period.startDate + 'T00:00:00',
                endDate: period.endDate + 'T23:59:59'
            };
            
            const response = await apiService.getBalanceForPeriod(formattedPeriod);
            // Правильная логика: к начальному бюджету прибавляется balance
            const currentBalance = response.balance || 0;
            setBalance(balanceLimit + currentBalance);
        } catch (error) {
            console.error('Ошибка загрузки баланса:', error);
            setBalance(balanceLimit);
        }
    };

    const fetchGeneralReport = async () => {
        setIsLoading(true);
        setError(null);

        try {
            // Преобразуем даты в правильный формат для бэкенда
            const formattedPeriod = {
                startDate: period.startDate + 'T00:00:00',
                endDate: period.endDate + 'T23:59:59'
            };

            const [balanceResponse, incomesResponse, expensesResponse, reportResponse] = await Promise.all([
                apiService.getBalanceForPeriod(formattedPeriod),
                apiService.getIncomesForPeriod(formattedPeriod),
                apiService.getExpensesForPeriod(formattedPeriod),
                apiService.getReportForPeriod(formattedPeriod)
            ]);

            console.log('API responses:', {
                balance: balanceResponse,
                incomes: incomesResponse,
                expenses: expensesResponse,
                report: reportResponse
            });

            // Правильно обрабатываем данные отчета
            const reportData = {
                totalIncome: incomesResponse.totalIncome || 0,
                totalExpense: expensesResponse.totalExpense || 0,
                categoryExpenses: {
                    income: {},
                    expense: {}
                }
            };

            // Преобразуем данные отчета в нужный формат
            if (reportResponse.incomesGrouped && Array.isArray(reportResponse.incomesGrouped)) {
                reportResponse.incomesGrouped.forEach(item => {
                    reportData.categoryExpenses.income[item.category] = parseFloat(item.sum);
                });
            }

            if (reportResponse.expensesGrouped && Array.isArray(reportResponse.expensesGrouped)) {
                reportResponse.expensesGrouped.forEach(item => {
                    reportData.categoryExpenses.expense[item.category] = parseFloat(item.sum);
                });
            }

            console.log('Processed report data:', reportData);

            setStats(reportData);
            
            // Инвертируем логику баланса: показываем остаток от лимита
            const currentBalance = balanceResponse.balance || 0;
            setBalance(balanceLimit + currentBalance);
        } catch (error) {
            setError('Ошибка загрузки отчета: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    const handleBudgetSubmit = (e) => {
        e.preventDefault();
        const newLimit = parseFloat(budgetForm.limit);
        if (newLimit > 0) {
            setBalanceLimit(newLimit);
            // Пересчитываем баланс с новым лимитом
            if (balance !== null) {
                const currentBalance = balance - balanceLimit; // Получаем исходный balance
                setBalance(newLimit + currentBalance);
            }
            setShowBudgetModal(false);
            setBudgetForm({ limit: '' });
            
            // Уведомляем родительский компонент
            notifyParent();
        }
    };

    useEffect(() => {
        fetchBalance();
    }, [period, balanceLimit]);

    useEffect(() => {
        // Автоматически загружаем отчет при изменении периода
        if (period.startDate && period.endDate) {
            fetchGeneralReport();
        }
    }, [period]);

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('ru-RU', {
            style: 'currency',
            currency: 'RUB'
        }).format(amount);
    };

    const getBalanceProgress = () => {
        if (!balanceLimit || balanceLimit <= 0) return 0;
        // Показываем процент истраченного бюджета
        const usedAmount = balanceLimit - balance;
        return Math.min((usedAmount / balanceLimit) * 100, 100);
    };

    const getBalanceStatus = () => {
        if (!balanceLimit) return 'success';
        const percentage = getBalanceProgress();
        // Инвертируем логику: больше 90% использовано = плохо
        if (percentage >= 90) return 'danger';
        if (percentage >= 70) return 'warning';
        return 'success';
    };

    return (
        <div className="stats-container">
            <div className="text-center mb-4">
                <h3>Финансовая статистика</h3>
            </div>

            {/* Период для отчета */}
            <Card className="mb-4">
                <Card.Body>
                    <div className="text-center mb-3">
                        <h5>Период отчета</h5>
                    </div>
                    <Row>
                        <Col md={6}>
                            <Form.Group className="mb-3">
                                <Form.Label>Начальная дата</Form.Label>
                                <Form.Control
                                    type="date"
                                    value={period.startDate}
                                    onChange={(e) => setPeriod(prev => ({ ...prev, startDate: e.target.value }))}
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mb-3">
                                <Form.Label>Конечная дата</Form.Label>
                                <Form.Control
                                    type="date"
                                    value={period.endDate}
                                    onChange={(e) => setPeriod(prev => ({ ...prev, endDate: e.target.value }))}
                                />
                            </Form.Group>
                        </Col>
                    </Row>
                    <div className="text-center">
                        
                    </div>
                </Card.Body>
            </Card>

            {error && (
                <Alert variant="danger" className="mb-4">
                    {error}
                </Alert>
            )}

            {/* Текущий баланс с лимитом */}
            {balance !== null && (
                <Card className="mb-4">
                    <Card.Body>
                        <div className="text-center">
                            <h5>Остаток бюджета</h5>
                            <h2 className={`text-${balance >= 0 ? 'success' : 'danger'}`}>
                                {formatCurrency(balance)}
                            </h2>

                            {balanceLimit && (
                                <div className="mt-3">
                                    <div className="d-flex justify-content-between mb-2">
                                        <span>Лимит: {formatCurrency(balanceLimit)}</span>
                                    </div>
                                    <ProgressBar
                                        variant={getBalanceStatus()}
                                        now={getBalanceProgress()}
                                        className="mb-2"
                                    />
                                    <div className="text-center mb-2">
                                        <span className="fw-bold">{Math.round(getBalanceProgress())}% истрачено</span>
                                    </div>
                                    <small className="text-muted text-center d-block">
                                        {balance <= 0
                                            ? 'Бюджет превышен!'
                                            : `Осталось: ${formatCurrency(balance)}`
                                        }
                                    </small>
                                    <div className="mt-2">
                                        <Button
                                            variant="outline-primary"
                                            size="sm"
                                            onClick={() => setShowBudgetModal(true)}
                                        >
                                            ✏️ Редактировать бюджет
                                        </Button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </Card.Body>
                </Card>
            )}

            {/* Общий отчет */}
            {stats && (
                <Row className="mb-4">
                    <Col md={6}>
                        <Card>
                            <Card.Body>
                                <div className="text-center">
                                    <h5>Доходы за период</h5>
                                    <h3 className="text-success">
                                        {formatCurrency(stats.totalIncome || 0)}
                                    </h3>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                    <Col md={6}>
                        <Card>
                            <Card.Body>
                                <div className="text-center">
                                    <h5>Расходы за период</h5>
                                    <h3 className="text-danger">
                                        {formatCurrency(stats.totalExpense || 0)}
                                    </h3>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            )}

            {/* Расходы по категориям и детальная статистика в одной строке */}
            {stats && (
                <Row className="mb-4">
                    {/* Детальная статистика */}
                    <Col md={12}>
                        <Card>
                            <Card.Body>
                                <h5 className="text-center mb-4">Детальная статистика за период</h5>
                                {stats && stats.categoryExpenses && (
                                    <Row>
                                        <Col md={6}>
                                            <div className="mb-3">
                                                <h6 className="text-success text-center mb-3">Доходы по категориям:</h6>
                                                {stats.categoryExpenses.income && Object.keys(stats.categoryExpenses.income).length > 0 ? (
                                                    Object.entries(stats.categoryExpenses.income).map(([category, amount]) => (
                                                        <div key={category} className="d-flex justify-content-between mb-2">
                                                            <span>{category}</span>
                                                            <span className="text-success fw-bold">{formatCurrency(amount)}</span>
                                                        </div>
                                                    ))
                                                ) : (
                                                    <p className="text-muted text-center">Нет доходов за выбранный период</p>
                                                )}
                                            </div>
                                        </Col>
                                        <Col md={6}>
                                            <div>
                                                <h6 className="text-danger text-center mb-3">Расходы по категориям:</h6>
                                                {stats.categoryExpenses.expense && Object.keys(stats.categoryExpenses.expense).length > 0 ? (
                                                    Object.entries(stats.categoryExpenses.expense).map(([category, amount]) => (
                                                        <div key={category} className="d-flex justify-content-between mb-2">
                                                            <span>{category}</span>
                                                            <span className="text-danger fw-bold">{formatCurrency(amount)}</span>
                                                        </div>
                                                    ))
                                                ) : (
                                                    <p className="text-muted text-center">Нет расходов за выбранный период</p>
                                                )}
                                            </div>
                                        </Col>
                                    </Row>
                                )}
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            )}

            {/* Модальное окно редактирования бюджета */}
            <Modal show={showBudgetModal} onHide={() => setShowBudgetModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Редактировать бюджет</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form onSubmit={handleBudgetSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>Лимит бюджета</Form.Label>
                            <Form.Control
                                type="number"
                                value={budgetForm.limit}
                                onChange={(e) => setBudgetForm({ limit: e.target.value })}
                                placeholder="Введите лимит бюджета"
                                step="0.01"
                                min="0.01"
                                required
                            />
                            <Form.Text className="text-muted">
                                Укажите максимальную сумму для отслеживания расходов
                            </Form.Text>
                        </Form.Group>
                    </Form>
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowBudgetModal(false)}>
                        Отмена
                    </Button>
                    <Button variant="primary" onClick={handleBudgetSubmit}>
                        Сохранить
                    </Button>
                </Modal.Footer>
            </Modal>
        </div>
    );
}

export default Stats; 