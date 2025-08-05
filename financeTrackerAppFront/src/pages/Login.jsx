import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Form, Button, Alert, Container, Row, Col, Card } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContext';

function Login() {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [errors, setErrors] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();
    const { login } = useAuth();

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const validateForm = () => {
        const newErrors = [];
        
        if (!formData.email) {
            newErrors.push('Email обязателен');
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.push('Некорректный формат email');
        }
        
        if (!formData.password) {
            newErrors.push('Пароль обязателен');
        } else if (formData.password.length < 6) {
            newErrors.push('Пароль должен содержать минимум 6 символов');
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
            const result = await login(formData);
            
            if (result.success) {
                navigate('/');
            } else {
                setErrors([result.error || 'Ошибка входа']);
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
                            Вход в систему
                        </Card.Header>
                        <Card.Body>
                            <Form onSubmit={handleSubmit}>
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
                                        {isLoading ? 'Вход...' : 'Войти'}
                                    </Button>
                                </div>
                            </Form>
                            
                            <div className="text-center mt-3">
                                <p>
                                    Нет аккаунта?{' '}
                                    <Link to="/signup">Зарегистрироваться</Link>
                                </p>
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>
        </Container>
    );
}

export default Login; 