import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import Header from './components/Header.jsx';
import Main from './pages/Main.jsx';
import Profile from './pages/Profile.jsx';
import Login from './pages/Login.jsx';
import Signup from './pages/Signup.jsx';

// Компонент для защищенных маршрутов
function ProtectedRoute({ children }) {
    const { isAuthenticated, isLoading } = useAuth();
    
    if (isLoading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Загрузка...</span>
                </div>
            </div>
        );
    }
    
    return isAuthenticated ? children : <Navigate to="/login" replace />;
}

// Компонент для публичных маршрутов (только для неавторизованных)
function PublicRoute({ children }) {
    const { isAuthenticated, isLoading } = useAuth();
    
    if (isLoading) {
        return (
            <div className="d-flex justify-content-center align-items-center" style={{ height: '100vh' }}>
                <div className="spinner-border" role="status">
                    <span className="visually-hidden">Загрузка...</span>
                </div>
            </div>
        );
    }
    
    return !isAuthenticated ? children : <Navigate to="/" replace />;
}

function AppRoutes() {
    const { logout } = useAuth();

    return (
        <Router>
            <Header onLogout={logout} />
            <div className="app-container">
                <Routes>
                    {/* Защищенные маршруты */}
                    <Route path="/" element={
                        <ProtectedRoute>
                            <Main />
                        </ProtectedRoute>
                    } />
                    <Route path="/profile" element={
                        <ProtectedRoute>
                            <Profile />
                        </ProtectedRoute>
                    } />
                    
                    {/* Публичные маршруты */}
                    <Route path="/login" element={
                        <PublicRoute>
                            <Login />
                        </PublicRoute>
                    } />
                    <Route path="/signup" element={
                        <PublicRoute>
                            <Signup />
                        </PublicRoute>
                    } />
                    
                    {/* Перенаправление на главную для несуществующих маршрутов */}
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </div>
        </Router>
    );
}

function App() {
    return (
        <AuthProvider>
            <AppRoutes />
        </AuthProvider>
    );
}

export default App;