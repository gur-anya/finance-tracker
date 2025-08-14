import { useState } from 'react';
import { Button, Form, Alert } from 'react-bootstrap';
import apiService from '../services/api';
import { getExpenseCategories, getIncomeCategories, getCategoryDisplayName } from '../utils/categoryMapper';

function AddTransactionCard({ onTransactionCreated }) {
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        description: '',
        category: 'ЕДА',
        sum: '',
        type: '0' // 0 - расход, 1 - доход
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    const categories = {
        '0': [...getExpenseCategories(), 'GOAL'],
        '1': [...getIncomeCategories(), 'GOAL']
    };

    // Временно используем моковую цель для тестирования
    const hasGoal = true; // В реальном приложении это будет проверяться через API

    const handleAddClick = () => {
        setIsEditing(true);
        setFormData({
            description: '',
            category: 'FOOD',
            sum: '',
            type: '0'
        });
        setErrors({});
    };

    const handleCancel = () => {
        setIsEditing(false);
        setFormData({
            description: '',
            category: 'FOOD',
            sum: '',
            type: '0'
        });
        setErrors({});
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        // Валидация
        const newErrors = {};
        if (!formData.description.trim()) {
            newErrors.description = 'Описание обязательно';
        }
        if (!formData.sum || parseFloat(formData.sum) <= 0) {
            newErrors.sum = 'Сумма должна быть больше 0';
        }
        if (!formData.category) {
            newErrors.category = 'Выберите категорию';
        }

        // Проверка для категории "GOAL"
        if (formData.category === 'GOAL' && !hasGoal) {
            newErrors.category = 'Сначала создайте финансовую цель в разделе "ЦЕЛЬ"';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setIsLoading(true);

        try {
            // Создаем данные транзакции для API
            const transactionData = {
                type: parseInt(formData.type),
                sum: parseFloat(formData.sum),
                category: formData.category,
                description: formData.description.trim()
            };

            // Вызываем callback для создания транзакции через API
            await onTransactionCreated(transactionData);
            
            // Сбрасываем форму
            setIsEditing(false);
            setFormData({
                description: '',
                category: 'FOOD',
                sum: '',
                type: '0'
            });
            setErrors({});
        } catch (error) {
            setErrors({ submit: 'Ошибка создания транзакции: ' + error.message });
        } finally {
            setIsLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
        
        // Очищаем ошибку при вводе
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: null
            }));
        }
    };

    if (isEditing) {
        return (
            <div className="transaction-card editing">
                <div className="edit-form">
                    <Form onSubmit={handleSubmit}>
                        <Form.Group className="mb-3">
                            <Form.Label>Тип транзакции</Form.Label>
                            <Form.Select
                                name="type"
                                value={formData.type}
                                onChange={handleInputChange}
                                isInvalid={!!errors.type}
                            >
                                <option value="0">Расход</option>
                                <option value="1">Доход</option>
                            </Form.Select>
                        </Form.Group>

                        <Form.Group className="mb-3">
                            <Form.Label>Описание</Form.Label>
                            <Form.Control
                                type="text"
                                name="description"
                                value={formData.description}
                                onChange={handleInputChange}
                                placeholder="Описание транзакции"
                                isInvalid={!!errors.description}
                            />
                            <Form.Control.Feedback type="invalid">
                                {errors.description}
                            </Form.Control.Feedback>
                        </Form.Group>
                        
                        <Form.Group className="mb-3">
                            <Form.Label>Категория</Form.Label>
                            <Form.Select
                                name="category"
                                value={formData.category}
                                onChange={handleInputChange}
                                isInvalid={!!errors.category}
                            >
                                {categories[formData.type].map(cat => (
                                    <option key={cat} value={cat}>{getCategoryDisplayName(cat)}</option>
                                ))}
                            </Form.Select>
                            <Form.Control.Feedback type="invalid">
                                {errors.category}
                            </Form.Control.Feedback>
                            {formData.category === 'GOAL' && !hasGoal && (
                                <Form.Text className="text-warning">
                                    ⚠️ Для использования категории "ЦЕЛЬ" сначала создайте финансовую цель
                                </Form.Text>
                            )}
                        </Form.Group>
                        
                        <Form.Group className="mb-3">
                            <Form.Label>Сумма</Form.Label>
                            <Form.Control
                                type="number"
                                name="sum"
                                value={formData.sum}
                                onChange={handleInputChange}
                                step="0.01"
                                min="0.01"
                                placeholder="0.00"
                                isInvalid={!!errors.sum}
                            />
                            <Form.Control.Feedback type="invalid">
                                {errors.sum}
                            </Form.Control.Feedback>
                        </Form.Group>

                        {errors.submit && (
                            <Alert variant="danger" className="mb-3">
                                {errors.submit}
                            </Alert>
                        )}
                        
                        <div className="d-flex gap-2">
                            <Button 
                                variant="success" 
                                type="submit"
                                className="flex-fill"
                                disabled={isLoading}
                            >
                                {isLoading ? 'Создание...' : '💾 Сохранить'}
                            </Button>
                            <Button 
                                variant="secondary" 
                                onClick={handleCancel}
                                className="flex-fill"
                                disabled={isLoading}
                            >
                                ❌ Отмена
                            </Button>
                        </div>
                    </Form>
                </div>
            </div>
        );
    }

    return (
        <div className="transaction-card add-card" onClick={handleAddClick}>
            <div className="add-card-content">
                <div className="add-icon">+</div>
                <div className="add-text">Добавить транзакцию</div>
            </div>
        </div>
    );
}

export default AddTransactionCard; 