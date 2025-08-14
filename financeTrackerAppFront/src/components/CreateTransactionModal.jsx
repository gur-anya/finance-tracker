import { useState } from 'react';
import { Modal, Form, Button, Alert } from 'react-bootstrap';
import apiService from '../services/api';
import { getExpenseCategories, getIncomeCategories, getCategoryDisplayName } from '../utils/categoryMapper';

function CreateTransactionModal({ show, onHide, onTransactionCreated }) {
    const [formData, setFormData] = useState({
        type: '0', // 0 - расход, 1 - доход
        sum: '',
        category: '',
        description: ''
    });
    const [errors, setErrors] = useState([]);
    const [isLoading, setIsLoading] = useState(false);

    const categories = {
        '0': getExpenseCategories(),
        '1': getIncomeCategories()
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const validateForm = () => {
        const newErrors = [];
        
        if (!formData.sum || parseFloat(formData.sum) <= 0) {
            newErrors.push('Сумма должна быть больше 0');
        }
        
        if (!formData.category) {
            newErrors.push('Выберите категорию');
        }
        
        if (!formData.description || formData.description.trim().length < 3) {
            newErrors.push('Описание должно содержать минимум 3 символа');
        }
        
        setErrors(newErrors);
        return newErrors.length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        setIsLoading(true);
        setErrors([]);

        try {
            const transactionData = {
                type: parseInt(formData.type),
                sum: parseFloat(formData.sum),
                category: formData.category,
                description: formData.description.trim()
            };

            await onTransactionCreated(transactionData);
            
            // Сброс формы
            setFormData({
                type: '0',
                sum: '',
                category: '',
                description: ''
            });
            onHide();
        } catch (error) {
            setErrors(['Ошибка создания транзакции: ' + error.message]);
        } finally {
            setIsLoading(false);
        }
    };

    const handleClose = () => {
        setFormData({
            type: '0',
            sum: '',
            category: '',
            description: ''
        });
        setErrors([]);
        onHide();
    };

    return (
        <Modal show={show} onHide={handleClose} size="lg">
            <Modal.Header closeButton>
                <Modal.Title>Создать транзакцию</Modal.Title>
            </Modal.Header>
            <Modal.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Тип транзакции</Form.Label>
                        <Form.Select
                            name="type"
                            value={formData.type}
                            onChange={handleChange}
                            required
                        >
                            <option value="0">Расход</option>
                            <option value="1">Доход</option>
                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Сумма</Form.Label>
                        <Form.Control
                            type="number"
                            name="sum"
                            value={formData.sum}
                            onChange={handleChange}
                            placeholder="Введите сумму"
                            step="0.01"
                            min="0.01"
                            required
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Категория</Form.Label>
                        <Form.Select
                            name="category"
                            value={formData.category}
                            onChange={handleChange}
                            required
                        >
                            <option value="">Выберите категорию</option>
                            {categories[formData.type].map(category => (
                                <option key={category} value={category}>
                                    {getCategoryDisplayName(category)}
                                </option>
                            ))}
                        </Form.Select>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Описание</Form.Label>
                        <Form.Control
                            as="textarea"
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            placeholder="Опишите транзакцию"
                            rows={3}
                            required
                        />
                    </Form.Group>

                    {errors.length > 0 && (
                        <Alert variant="danger">
                            <ul className="mb-0">
                                {errors.map((error, index) => (
                                    <li key={index}>{error}</li>
                                ))}
                            </ul>
                        </Alert>
                    )}

                    <div className="d-flex justify-content-end gap-2">
                        <Button variant="secondary" onClick={handleClose}>
                            Отмена
                        </Button>
                        <Button 
                            type="submit" 
                            variant="success" 
                            disabled={isLoading}
                        >
                            {isLoading ? 'Создание...' : 'Создать'}
                        </Button>
                    </div>
                </Form>
            </Modal.Body>
        </Modal>
    );
}

export default CreateTransactionModal; 