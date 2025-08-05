import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Button, Alert, Container, Row, Col, Card } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContext';

function Signup() {
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        confirmPassword: ''
    });
    const [errors, setErrors] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const { signup } = useAuth();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const validateForm = () => {
        const newErrors = [];
        
        if (!formData.name || formData.name.trim().length < 2) {
            newErrors.push('Имя должно содержать минимум 2 символа');
        }
        
        if (!formData.email) {
            newErrors.push('Email обязателен');
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.push('Некорректный формат email');
        }
        
        if (!formData.password) {
            newErrors.push('Пароль обязателен');
        } else if (formData.password.length < 8) {
            newErrors.push('Пароль должен содержать минимум 8 символов');
        } else if (!/(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[a-z])(?=.*[A-Z])/.test(formData.password)) {
            newErrors.push('Пароль должен содержать цифры, спецсимволы, строчные и заглавные буквы');
        }
        
        if (formData.password !== formData.confirmPassword) {
            newErrors.push('Пароли не совпадают');
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
            const result = await signup({
                name: formData.name,
                email: formData.email,
                password: formData.password
            });
            
            if (result.success) {
                navigate('/');
            } else {
                setErrors([result.error || 'Ошибка регистрации']);
            }
        } catch (error) {
            setErrors(['Ошибка соединения с сервером']);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Container className="mt-5">
            <Row className="justify-content-center">
                <Col md={6} lg={4}>
                    <Card>
                        <Card.Header as="h4" className="text-center">
                            Регистрация
                        </Card.Header>
                        <Card.Body>
                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3">
                                    <Form.Label>Имя</Form.Label>
                                    <Form.Control
                                        type="text"
                                        name="name"
                                        value={formData.name}
                                        onChange={handleChange}
                                        placeholder="Введите ваше имя"
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Email</Form.Label>
                                    <Form.Control
                                        type="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleChange}
                                        placeholder="Введите email"
                                        required
                                    />
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Пароль</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="password"
                                        value={formData.password}
                                        onChange={handleChange}
                                        placeholder="Введите пароль"
                                        required
                                    />
                                    <Form.Text className="text-muted">
                                        Минимум 8 символов, включая цифры, спецсимволы, строчные и заглавные буквы
                                    </Form.Text>
                                </Form.Group>

                                <Form.Group className="mb-3">
                                    <Form.Label>Подтвердите пароль</Form.Label>
                                    <Form.Control
                                        type="password"
                                        name="confirmPassword"
                                        value={formData.confirmPassword}
                                        onChange={handleChange}
                                        placeholder="Повторите пароль"
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

                                <div className="d-grid gap-2">
                                    <Button 
                                        type="submit" 
                                        variant="success" 
                                        disabled={isLoading}
                                        className="w-100"
                                    >
                                        {isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
                                    </Button>
                                </div>
                            </Form>
                            
                            <div className="text-center mt-3">
                                <p>
                                    Уже есть аккаунт?{' '}
                                    <Link to="/login">Войти</Link>
                                </p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default Signup; 