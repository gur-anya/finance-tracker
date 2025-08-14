import { useState, useEffect } from 'react';
import { Card, Row, Col, Button, Form, Alert, Spinner, ProgressBar } from 'react-bootstrap';
import apiService from '../services/api';

function Goals({ onGoalChange, onGoalTransactionsCleared }) {
    const [goal, setGoal] = useState(null);
    const [savedAmount, setSavedAmount] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showGoalForm, setShowGoalForm] = useState(false);
    const [goalForm, setGoalForm] = useState({
        title: '',
        targetAmount: ''
    });
    const [clearLoading, setClearLoading] = useState(false);
    const [clearSuccess, setClearSuccess] = useState(null);

    // Функция для уведомления родительского компонента об изменениях
    const notifyParent = () => {
        if (onGoalChange) {
            onGoalChange();
        }
    };

    const fetchGoal = async () => {
        setIsLoading(true);
        try {
            const response = await apiService.getUserGoal();
            console.log('Goal response:', response);
            if (response && response.goalName && response.goalSum) {
                // Преобразуем ответ бэкенда в формат фронтенда
                setGoal({
                    title: response.goalName,
                    targetAmount: response.goalSum
                });
                
                // Получаем реальные накопления из транзакций с категорией "ЦЕЛЬ"
                await fetchSavedAmount();
            } else {
                setGoal(null);
                setSavedAmount(0);
            }
        } catch (error) {
            console.error('Ошибка загрузки цели:', error);
            setGoal(null);
            setSavedAmount(0);
        } finally {
            setIsLoading(false);
        }
    };

    const fetchSavedAmount = async () => {
        try {
            console.log('Fetching saved amount...');
            // Используем новую API функцию для получения накоплений
            const response = await apiService.getGoalSavings();
            console.log('Goal savings response:', response);
            setSavedAmount(response.savedAmount || 0);
        } catch (error) {
            console.error('Ошибка загрузки накоплений:', error);
            setSavedAmount(0);
        }
    };

    const createGoal = async (goalData) => {
        setIsLoading(true);
        setError(null);
        
        try {
            const response = await apiService.setUserGoal({
                title: goalData.title,
                targetAmount: parseFloat(goalData.targetAmount)
            });
            
            // Преобразуем ответ бэкенда в формат фронтенда
            setGoal({
                title: response.goalName,
                targetAmount: response.goalSum
            });
            setSavedAmount(0); // Новая цель, накоплений пока нет
            setShowGoalForm(false);
            setGoalForm({ title: '', targetAmount: '' });
            
            // Уведомляем родительский компонент
            notifyParent();
        } catch (error) {
            setError('Ошибка создания цели: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    const updateGoal = async (updatedGoal) => {
        setIsLoading(true);
        setError(null);
        
        try {
            const payload = {};
            if (updatedGoal.title) payload.title = updatedGoal.title;
            if (updatedGoal.targetAmount) payload.targetAmount = updatedGoal.targetAmount;
            const response = await apiService.updateUserGoal(payload);
            
            // Преобразуем ответ бэкенда в формат фронтенда
            setGoal({
                title: response.goalName,
                targetAmount: response.goalSum
            });
            // Обновляем накопления после изменения цели
            await fetchSavedAmount();
            setShowGoalForm(false);
            
            // Уведомляем родительский компонент
            notifyParent();
        } catch (error) {
            setError('Ошибка обновления цели: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    const deleteGoal = async () => {
        setIsLoading(true);
        setError(null);
        
        try {
            await apiService.resetUserGoal();
            setGoal(null);
            setSavedAmount(0);
            
            // Уведомляем родительский компонент
            notifyParent();
        } catch (error) {
            setError('Ошибка удаления цели: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    // Новый обработчик для удаления всех транзакций цели
    const handleClearGoalTransactions = async () => {
        setClearLoading(true);
        setClearSuccess(null);
        setError(null);
        try {
            await apiService.clearGoalTransactions();
            setClearSuccess('Все транзакции с категорией "ЦЕЛЬ" успешно удалены.');
            // Обновляем накопления и цель
            await fetchSavedAmount();
            notifyParent();
            // Динамически удаляем транзакции цели из списка (Main.jsx)
            if (onGoalTransactionsCleared) {
                onGoalTransactionsCleared();
            }
        } catch (error) {
            setError('Ошибка при удалении транзакций цели: ' + error.message);
        } finally {
            setClearLoading(false);
        }
    };

    useEffect(() => {
        fetchGoal();
    }, []);

    const formatCurrency = (amount) => {
        return new Intl.NumberFormat('ru-RU', {
            style: 'currency',
            currency: 'RUB'
        }).format(amount);
    };

    const getProgress = () => {
        if (!goal || goal.targetAmount <= 0) return 0;
        return Math.min((savedAmount / goal.targetAmount) * 100, 100);
    };

    const getProgressStatus = () => {
        const percentage = getProgress();
        if (percentage >= 100) return 'success';
        if (percentage >= 75) return 'info';
        if (percentage >= 50) return 'warning';
        return 'danger';
    };

    const getGoalMessage = () => {
        const percentage = getProgress();
        if (percentage >= 100) {
            return {
                text: '🎉 Поздравляем! Цель достигнута! Может, пора поставить новую цель?',
                variant: 'success'
            };
        } else if (percentage >= 50) {
            return {
                text: '💪 Продолжайте в том же духе! Вы на полпути к цели!',
                variant: 'info'
            };
        } else {
            return {
                text: '🌟 У вас все получится! Каждый шаг приближает к цели!',
                variant: 'warning'
            };
        }
    };

    const handleSubmitGoal = (e) => {
        e.preventDefault();
        
        if (!goalForm.title.trim() || !goalForm.targetAmount) {
            setError('Все поля обязательны для заполнения');
            return;
        }

        if (parseFloat(goalForm.targetAmount) <= 0) {
            setError('Сумма должна быть больше 0');
            return;
        }

        createGoal(goalForm);
    };

    if (isLoading) {
        return (
            <div className="text-center p-5">
                <Spinner animation="border" role="status">
                    <span className="visually-hidden">Загрузка...</span>
                </Spinner>
                <p className="mt-2">Загрузка целей...</p>
            </div>
        );
    }

    return (
        <div className="stats-container">
            <div className="text-center mb-4">
                <h3>Финансовые цели</h3>
            </div>

            {error && (
                <Alert variant="danger" className="mb-4" onClose={() => setError(null)} dismissible>
                    {error}
                </Alert>
            )}

            {clearSuccess && (
                <Alert variant="success" className="mb-4" onClose={() => setClearSuccess(null)} dismissible>
                    {clearSuccess}
                </Alert>
            )}

            {!goal ? (
                // Форма создания цели
                <Card>
                    <Card.Body>
                        <div className="text-center mb-4">
                            <h5>Создать новую цель</h5>
                            <p className="text-muted">
                                Установите финансовую цель и отслеживайте прогресс накоплений
                            </p>
                        </div>
                        
                        <Form onSubmit={handleSubmitGoal}>
                            <Form.Group className="mb-3">
                                <Form.Label>Название цели *</Form.Label>
                                <Form.Control
                                    type="text"
                                    value={goalForm.title}
                                    onChange={(e) => setGoalForm(prev => ({ ...prev, title: e.target.value }))}
                                    placeholder="Например: Накопить на отпуск"
                                    required
                                />
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Целевая сумма *</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={goalForm.targetAmount}
                                    onChange={(e) => setGoalForm(prev => ({ ...prev, targetAmount: e.target.value }))}
                                    placeholder="Введите сумму"
                                    step="0.01"
                                    min="0.01"
                                    required
                                />
                            </Form.Group>

                            <div className="text-center">
                                <Button 
                                    type="submit" 
                                    variant="success" 
                                    disabled={isLoading}
                                >
                                    {isLoading ? 'Создание...' : 'Создать цель'}
                                </Button>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            ) : (
                // Отображение существующей цели
                <Row>
                    <Col md={8}>
                        <Card className="mb-4">
                            <Card.Body>
                                <div className="text-center">
                                    <h4>{goal.title}</h4>
                                    <div className="mb-3">
                                        <h2 className="text-success">
                                            {formatCurrency(savedAmount)}
                                        </h2>
                                        <p className="text-muted">
                                            из {formatCurrency(goal.targetAmount)}
                                        </p>
                                    </div>
                                    
                                    <div className="mb-3">
                                        <div className="d-flex justify-content-between mb-2">
                                            <span>Прогресс</span>
                                            <span>{Math.round(getProgress())}%</span>
                                        </div>
                                        <ProgressBar 
                                            variant={getProgressStatus()} 
                                            now={getProgress()} 
                                            className="mb-2"
                                        />
                                    </div>
                                    
                                    {/* Сообщение с поздравлением */}
                                    <div className="mb-3">
                                        <Alert variant={getGoalMessage().variant} className="text-center">
                                            {getGoalMessage().text}
                                        </Alert>
                                    </div>
                                    
                                    <div className="row text-center">
                                        <div className="col-12">
                                            <h6>Осталось накопить</h6>
                                            <p className="text-primary fw-bold">
                                                {formatCurrency(Math.max(0, goal.targetAmount - savedAmount))}
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                    
                    <Col md={4}>
                        <Card>
                            <Card.Body>
                                <h5 className="text-center mb-3">Управление целью</h5>
                                
                                <div className="d-grid gap-2">
                                    <Button 
                                        variant="outline-success" 
                                        onClick={() => setShowGoalForm(true)}
                                    >
                                        ✏️ Редактировать
                                    </Button>
                                    <Button 
                                        variant="outline-danger" 
                                        onClick={deleteGoal}
                                    >
                                        🗑️ Удалить цель
                                    </Button>
                                    <Button
                                        variant="outline-warning"
                                        onClick={handleClearGoalTransactions}
                                        disabled={clearLoading}
                                    >
                                        {clearLoading ? 'Удаление...' : 'Удалить все транзакции цели'}
                                    </Button>
                                </div>
                                
                                <hr />
                                
                                <div className="text-center">
                                    <small className="text-muted">
                                        <strong>Важно:</strong> Транзакции с категорией "ЦЕЛЬ" 
                                        автоматически добавляются к накоплениям
                                    </small>
                                </div>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            )}

            {/* Модальное окно редактирования цели */}
            {showGoalForm && goal && (
                <Card className="mt-4">
                    <Card.Body>
                        <h5 className="text-center mb-3">Редактировать цель</h5>
                        <Form onSubmit={(e) => {
                            e.preventDefault();
                            updateGoal({
                                ...goal,
                                title: goalForm.title,
                                targetAmount: goalForm.targetAmount
                            });
                            setShowGoalForm(false);
                        }}>
                            <Form.Group className="mb-3">
                                <Form.Label>Название цели</Form.Label>
                                <Form.Control
                                    type="text"
                                    value={goalForm.title}
                                    onChange={(e) => setGoalForm(prev => ({ ...prev, title: e.target.value }))}
                                    placeholder="Название цели"
                                />
                            </Form.Group>

                            <Form.Group className="mb-3">
                                <Form.Label>Целевая сумма</Form.Label>
                                <Form.Control
                                    type="number"
                                    value={goalForm.targetAmount}
                                    onChange={(e) => setGoalForm(prev => ({ ...prev, targetAmount: e.target.value }))}
                                    placeholder="Сумма"
                                    step="0.01"
                                    min="0.01"
                                />
                            </Form.Group>

                            <div className="d-flex gap-2">
                                <Button 
                                    type="submit" 
                                    variant="success" 
                                    className="flex-fill"
                                    disabled={isLoading}
                                >
                                    {isLoading ? 'Сохранение...' : 'Сохранить'}
                                </Button>
                                <Button 
                                    variant="secondary" 
                                    className="flex-fill"
                                    onClick={() => setShowGoalForm(false)}
                                >
                                    Отмена
                                </Button>
                            </div>
                        </Form>
                    </Card.Body>
                </Card>
            )}
        </div>
    );
}

export default Goals; 