import { useState } from 'react';
import { Button, Modal, Alert, Form } from 'react-bootstrap';
import { getExpenseCategories, getIncomeCategories, getCategoryDisplayName } from '../utils/categoryMapper';

function TransactionCard({ id, type, timestamp, category, sum, description, onDelete, onEdit }) {
    // Функции должны быть объявлены в начале компонента
    const getTypeDisplay = (type) => {
        // Проверяем различные форматы типа транзакции
        const typeValue = String(type).toLowerCase();
        return typeValue === '1' || typeValue === 'income' || typeValue === 'true' ? 'Доход' : 'Расход';
    };

    const getTypeColor = (type) => {
        // Проверяем различные форматы типа транзакции
        const typeValue = String(type).toLowerCase();
        return typeValue === '1' || typeValue === 'income' || typeValue === 'true' ? 'success' : 'danger';
    };

    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [isEditing, setIsEditing] = useState(false);
    const [error, setError] = useState(null);
    const [editForm, setEditForm] = useState({
        description: description,
        category: category,
        sum: sum,
        type: type
    });

    const formattedSum = Number(sum).toFixed(2);
    const formattedTimestamp = formatTimestamp(timestamp);

    const handleDelete = async () => {
        if (!id) {
            setError('ID транзакции не найден');
            return;
        }
        
        setIsDeleting(true);
        setError(null);
        
        try {
            await onDelete(id);
            setShowDeleteModal(false);
        } catch (error) {
            setError('Ошибка удаления транзакции: ' + error.message);
        } finally {
            setIsDeleting(false);
        }
    };

    const handleEdit = () => {
        // Убеждаемся, что категория корректна для текущего типа
        const currentType = String(type).toLowerCase();
        const typeKey = currentType === '1' || currentType === 'income' || currentType === 'true' ? '1' : '0';
        const availableCategories = categories[typeKey] || [];
        
        setEditForm({
            description: description,
            category: availableCategories.includes(category) ? category : availableCategories[0] || 'OTHER',
            sum: sum,
            type: typeKey
        });
        setIsEditing(true);
    };

    const handleSaveEdit = async () => {
        if (!id) {
            setError('ID транзакции не найден');
            return;
        }
        
        try {
            const updatedData = {
                description: editForm.description,
                category: editForm.category,
                sum: parseFloat(editForm.sum),
                type: parseInt(editForm.type)
            };
            
            await onEdit(id, updatedData);
            setIsEditing(false);
        } catch (error) {
            setError('Ошибка обновления транзакции: ' + error.message);
        }
    };

    const handleCancelEdit = () => {
        // Убеждаемся, что категория корректна для текущего типа
        const currentType = String(type).toLowerCase();
        const typeKey = currentType === '1' || currentType === 'income' || currentType === 'true' ? '1' : '0';
        const availableCategories = categories[typeKey] || [];
        
        setEditForm({
            description: description,
            category: availableCategories.includes(category) ? category : availableCategories[0] || 'OTHER',
            sum: sum,
            type: typeKey
        });
        setIsEditing(false);
    };

    const handleEditChange = (e) => {
        const { name, value } = e.target;
        setEditForm(prev => {
            const newForm = {
                ...prev,
                [name]: value
            };
            
            // Если изменился тип транзакции, сбрасываем категорию на первую доступную
            if (name === 'type') {
                const availableCategories = categories[value] || [];
                if (availableCategories.length > 0 && !availableCategories.includes(newForm.category)) {
                    newForm.category = availableCategories[0];
                }
            }
            
            return newForm;
        });
    };

    const categories = {
        '0': [...getExpenseCategories(), 'GOAL'],
        '1': [...getIncomeCategories(), 'GOAL']
    };

    // Получаем доступные категории для текущего типа
    const availableCategories = categories[editForm.type] || [];

    return (
        <>
            <div className={`transaction-card ${isEditing ? 'editing' : ''}`}>
                {!isEditing ? (
                    <>
                        <div className="transaction-info">
                            <div className="transaction-title">{description}</div>
                            <div className="transaction-category">{category}</div>
                            <div className={`transaction-sum text-${getTypeColor(type)}`}>
                                {formattedSum} ₽
                            </div>
                            <div className="transaction-time">{formattedTimestamp}</div>
                        </div>
                        
                        <div className="transaction-actions">
                            <button 
                                className="action-button edit-button"
                                onClick={handleEdit}
                                title="Редактировать"
                            >
                                ✏️
                            </button>
                            <button 
                                className="action-button delete-button"
                                onClick={() => setShowDeleteModal(true)}
                                title="Удалить"
                            >
                                🗑️
                            </button>
                        </div>
                    </>
                ) : (
                    <div className="edit-form">
                        <Form.Group>
                            <Form.Label>Описание</Form.Label>
                            <Form.Control
                                type="text"
                                name="description"
                                value={editForm.description}
                                onChange={handleEditChange}
                                placeholder="Описание транзакции"
                            />
                        </Form.Group>
                        
                        <Form.Group>
                            <Form.Label>Тип транзакции</Form.Label>
                            <Form.Select
                                name="type"
                                value={editForm.type}
                                onChange={handleEditChange}
                            >
                                <option value={1}>Доход</option>
                                <option value={0}>Расход</option>
                            </Form.Select>
                        </Form.Group>
                        
                        <Form.Group>
                            <Form.Label>Категория</Form.Label>
                            <Form.Select
                                name="category"
                                value={editForm.category}
                                onChange={handleEditChange}
                            >
                                {availableCategories.map(cat => (
                                    <option key={cat} value={cat}>{getCategoryDisplayName(cat)}</option>
                                ))}
                            </Form.Select>
                        </Form.Group>
                        
                        <Form.Group>
                            <Form.Label>Сумма</Form.Label>
                            <Form.Control
                                type="number"
                                name="sum"
                                value={editForm.sum}
                                onChange={handleEditChange}
                                step="0.01"
                                min="0.01"
                            />
                        </Form.Group>
                        
                        <div className="d-flex gap-2 mt-2">
                            <Button 
                                variant="success" 
                                size="sm" 
                                onClick={handleSaveEdit}
                            >
                                💾 Сохранить
                            </Button>
                            <Button 
                                variant="secondary" 
                                size="sm" 
                                onClick={handleCancelEdit}
                            >
                                ❌ Отмена
                            </Button>
                        </div>
                    </div>
                )}
            </div>

            {/* Модальное окно подтверждения удаления */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Подтверждение удаления</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>Вы уверены, что хотите удалить эту транзакцию?</p>
                    <div className="border rounded p-3 bg-light">
                        <div className="text-center">
                            <strong>{getTypeDisplay(type)}</strong><br/>
                            <span className="fw-bold">{description}</span><br/>
                            <span className="text-muted">{category}</span><br/>
                            <span className={`text-${getTypeColor(type)} fw-bold fs-5`}>
                                {formattedSum} ₽
                            </span><br/>
                            <small className="text-muted">{formattedTimestamp}</small>
                        </div>
                    </div>
                    
                    {error && (
                        <Alert variant="danger" className="mt-2">
                            {error}
                        </Alert>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Отмена
                    </Button>
                    <Button 
                        variant="danger" 
                        onClick={handleDelete}
                        disabled={isDeleting}
                    >
                        {isDeleting ? 'Удаление...' : '🗑️ Удалить'}
                    </Button>
                </Modal.Footer>
            </Modal>
        </>
    );
}

function formatTimestamp(timestamp) {
    if (!timestamp) return 'Дата не указана';
    
    const date = new Date(timestamp);
    
    if (isNaN(date.getTime())) {
        return 'Некорректная дата';
    }

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');

    return `${day}.${month}.${year} ${hours}:${minutes}`;
}

export default TransactionCard;