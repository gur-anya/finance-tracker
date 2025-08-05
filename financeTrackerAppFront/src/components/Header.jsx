import { Link, useNavigate } from 'react-router-dom';
import { Button, Navbar, Container, Nav } from 'react-bootstrap';
import { useAuth } from '../contexts/AuthContext';

function Header({ onLogout }) {
    const navigate = useNavigate();
    const { user, isAuthenticated } = useAuth();
    
    const handleLogout = async () => {
        try {
            await onLogout();
            navigate('/login');
        } catch (error) {
            console.error('Ошибка при выходе:', error);
            navigate('/login');
        }
    };

    if (!isAuthenticated) {
        return null; // Не показываем хедер для неавторизованных пользователей
    }

    return (
        <Navbar bg="success" variant="dark" expand="lg" className="mb-4">
            <Container fluid>
                {/* Логотип слева */}
                <Navbar.Brand as={Link} to="/" className="me-0">
                    💰 Finance Tracker
                </Navbar.Brand>
                
                <Navbar.Toggle aria-controls="basic-navbar-nav" />
                <Navbar.Collapse id="basic-navbar-nav">
                    {/* Навигация по центру */}
                    <Nav className="mx-auto">
                        <Nav.Link as={Link} to="/" className="px-4">
                            🏠 Главная
                        </Nav.Link>
                    </Nav>
                    
                    {/* Профиль справа */}
                    <Nav>
                        <Nav.Link as={Link} to="/profile" className="me-3">
                            👤 {user?.name ? `Здравствуйте, ${user.name}!` : (user?.email || 'Профиль')}
                        </Nav.Link>
                        <Button 
                            variant="outline-light" 
                            onClick={handleLogout}
                            size="sm"
                        >
                            Выйти
                        </Button>
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default Header;