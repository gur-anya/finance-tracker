import { useState, useEffect } from "react";
import { Form, Button, Alert, Card, Row, Col, Container } from "react-bootstrap";
import { useAuth } from '../contexts/AuthContext';
import apiService from '../services/api';

function Profile() {
    const { user, logout } = useAuth();
    const [userData, setUserData] = useState({
        name: user?.name || 'Пользователь',
        email: user?.email || ''
    });
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    useEffect(() => {
        if (user) {
            setUserData({
                name: user.name || 'Пользователь',
                email: user.email || ''
            });
        }
    }, [user]);

    return (
        <Container className="mt-4">
            <h2 className="mb-4">Профиль пользователя</h2>

            {isLoading ? (
                <div className="text-center">
                    <div className="spinner-border" role="status">
                        <span className="visually-hidden">Загрузка...</span>
                    </div>
                    <p className="mt-2">Загрузка профиля...</p>
                </div>
            ) : (
                <Row>
                    <Col md={6}>
                        <EditProfile
                            userData={userData}
                            onSuccess={(message) => setSuccess(message)}
                            onError={(message) => setError(message)}
                        />
                    </Col>
                    <Col md={6}>
                        <DeleteProfile
                            onSuccess={(message) => {
                                setSuccess(message);
                                logout();
                            }}
                            onError={(message) => setError(message)}
                        />
                    </Col>
                </Row>
            )}

            {error && (
                <Alert variant="danger" className="mt-3" onClose={() => setError(null)} dismissible>
                    {error}
                </Alert>
            )}

            {success && (
                <Alert variant="success" className="mt-3" onClose={() => setSuccess(null)} dismissible>
                    {success}
                </Alert>
            )}
        </Container>
    );
}

function EditProfile({ userData, onSuccess, onError }) {
    const { user, updateUserData } = useAuth();
    const [formData, setFormData] = useState({
        name: userData.name,
        newPassword: '',
        oldPassword: ''
    });
    const [showOldPassword, setShowOldPassword] = useState(false);
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        setFormData(prev => ({
            ...prev,
            name: userData.name
        }));
    }, [userData]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));

        // Очищаем ошибку для этого поля
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: null
            }));
        }
    };

    const validateForm = () => {
        const newErrors = {};

        // Валидация имени - минимум 3 символа
        if (!formData.name || formData.name.trim().length < 3) {
            newErrors.name = 'Имя должно содержать минимум 3 символа';
        }

        // Валидация пароля (если указан)
        if (formData.newPassword && formData.newPassword.length < 8) {
            newErrors.newPassword = 'Пароль должен содержать минимум 8 символов';
        } else if (formData.newPassword && !/(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])/.test(formData.newPassword)) {
            newErrors.newPassword = 'Пароль должен содержать цифры, спецсимволы, строчные и заглавные буквы';
        }

        if (formData.newPassword && !formData.oldPassword) {
            newErrors.oldPassword = 'Для смены пароля необходимо указать старый пароль';
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }

        setIsLoading(true);
        
        try {
            // Подготавливаем данные для обновления
            const updateData = {
                name: formData.name.trim()
            };
            
            // Добавляем пароль только если он указан
            if (formData.newPassword) {
                updateData.oldPassword = formData.oldPassword;
                updateData.newPassword = formData.newPassword;
            }
            
            // Используем ID пользователя
            await apiService.updateUser(user.id, updateData);
            
            // Обновляем данные пользователя в контексте
            await updateUserData();
            
            onSuccess('Профиль успешно обновлен');
            setFormData(prev => ({
                ...prev,
                newPassword: '',
                oldPassword: ''
            }));
            setShowOldPassword(false);
        } catch (error) {
            onError('Ошибка обновления профиля: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Card>
            <Card.Header>
                <h5>Редактировать профиль</h5>
            </Card.Header>
            <Card.Body>
                <Form onSubmit={handleSubmit}>
                    <Form.Group className="mb-3">
                        <Form.Label>Имя *</Form.Label>
                        <Form.Control
                            type="text"
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            placeholder="Введите ваше имя (минимум 3 символа)"
                            isInvalid={!!errors.name}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            {errors.name}
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Email</Form.Label>
                        <Form.Control
                            type="email"
                            value={userData.email}
                            disabled
                            className="bg-light"
                        />
                    </Form.Group>

                    <Form.Group className="mb-3">
                        <Form.Label>Новый пароль</Form.Label>
                        <Form.Control
                            type="password"
                            name="newPassword"
                            value={formData.newPassword}
                            onChange={(e) => {
                                handleChange(e);
                                if (e.target.value && !showOldPassword) {
                                    setShowOldPassword(true);
                                } else if (!e.target.value) {
                                    setShowOldPassword(false);
                                }
                            }}
                            placeholder="Введите новый пароль"
                            isInvalid={!!errors.newPassword}
                        />
                        <Form.Text className="text-muted">
                            Оставьте пустым, если не хотите менять пароль
                        </Form.Text>
                        <Form.Control.Feedback type="invalid">
                            {errors.newPassword}
                        </Form.Control.Feedback>
                    </Form.Group>

                    {showOldPassword && (
                        <Form.Group className="mb-3">
                            <Form.Label>Текущий пароль</Form.Label>
                            <Form.Control
                                type="password"
                                name="oldPassword"
                                value={formData.oldPassword}
                                onChange={handleChange}
                                placeholder="Введите текущий пароль"
                                isInvalid={!!errors.oldPassword}
                            />
                            <Form.Control.Feedback type="invalid">
                                {errors.oldPassword}
                            </Form.Control.Feedback>
                        </Form.Group>
                    )}

                    <Button
                        type="submit"
                        variant="success"
                        disabled={isLoading}
                        className="w-100"
                    >
                        {isLoading ? 'Сохранение...' : 'Сохранить изменения'}
                    </Button>
                </Form>
            </Card.Body>
        </Card>
    );
}

function DeleteProfile({ onSuccess, onError }) {
    const { user } = useAuth();
    const [showConfirm, setShowConfirm] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const handleDelete = async () => {
        setIsLoading(true);
        
        try {
            // Используем ID пользователя
            await apiService.deleteUser(user.id);
            
            onSuccess('Аккаунт успешно удален');
        } catch (error) {
            onError('Ошибка удаления аккаунта: ' + error.message);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Card className="border-danger">
            <Card.Header className="bg-danger text-white">
                <h5>Удалить аккаунт</h5>
            </Card.Header>
            <Card.Body>
                <p className="text-muted">
                    Это действие нельзя отменить. Все ваши данные будут безвозвратно удалены.
                </p>

                {!showConfirm ? (
                    <Button
                        variant="outline-danger"
                        onClick={() => setShowConfirm(true)}
                        className="w-100"
                    >
                        Удалить аккаунт
                    </Button>
                ) : (
                    <div>
                        <p className="text-danger fw-bold mb-3">
                            Вы уверены, что хотите удалить аккаунт?
                        </p>
                        <div className="d-flex gap-2">
                            <Button
                                variant="secondary"
                                onClick={() => setShowConfirm(false)}
                                className="flex-fill"
                            >
                                Отмена
                            </Button>
                            <Button
                                variant="danger"
                                onClick={handleDelete}
                                disabled={isLoading}
                                className="flex-fill"
                            >
                                {isLoading ? 'Удаление...' : 'Да, удалить'}
                            </Button>
                        </div>
                    </div>
                )}
            </Card.Body>
        </Card>
    );
}

export default Profile;