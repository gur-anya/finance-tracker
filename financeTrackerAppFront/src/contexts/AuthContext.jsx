import { createContext, useContext, useState, useEffect } from 'react';
import apiService from '../services/api';

const AuthContext = createContext();

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // Проверяем наличие токена при загрузке приложения
        const token = localStorage.getItem('token');
        const savedUser = localStorage.getItem('user');
        
        if (token && savedUser) {
            try {
                const userData = JSON.parse(savedUser);
                console.log('Loaded user data from localStorage:', userData);
                setUser(userData);
                setIsAuthenticated(true);
            } catch (error) {
                console.error('Error parsing user data:', error);
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
        }
        
        setIsLoading(false);
    }, []);

    const login = async (credentials) => {
        try {
            const response = await apiService.login(credentials);
            
            // Сохраняем токен
            localStorage.setItem('token', response.token);
            
            // Получаем email из токена
            const userData = await apiService.getCurrentUser();
            
            // Получаем полные данные пользователя по ID
            const fullUserData = await apiService.getUserById(response.id);
            
            // Сохраняем данные пользователя с ID из ответа логина
            const userToSave = {
                email: userData.email,
                name: fullUserData.name, // Реальное имя из бэкенда
                id: response.id // ID из LoginResponseDTO
            };
            
            console.log('Saving user data:', userToSave);
            localStorage.setItem('user', JSON.stringify(userToSave));
            setUser(userToSave);
            setIsAuthenticated(true);
            
            return { success: true };
        } catch (error) {
            // В случае ошибки очищаем токен
            localStorage.removeItem('token');
            return { success: false, error: error.message };
        }
    };

    const signup = async (userData) => {
        try {
            const response = await apiService.signup(userData);
            
            // После успешной регистрации автоматически входим
            const loginResult = await login({
                email: userData.email,
                password: userData.password
            });
            
            if (!loginResult.success) {
                // Если логин не удался, очищаем токен
                localStorage.removeItem('token');
                localStorage.removeItem('user');
            }
            
            return loginResult;
        } catch (error) {
            return { success: false, error: error.message };
        }
    };

    const logout = async () => {
        try {
            await apiService.logout();
        } catch (error) {
            console.error('Logout error:', error);
        } finally {
            // Очищаем данные независимо от результата запроса
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            setUser(null);
            setIsAuthenticated(false);
        }
    };

    const updateUserData = async () => {
        try {
            const currentUser = JSON.parse(localStorage.getItem('user') || '{}');
            if (!currentUser.id) {
                throw new Error('ID пользователя не найден');
            }
            
            // Получаем актуальные данные пользователя
            const userData = await apiService.getUserById(currentUser.id);
            
            // Обновляем данные, сохраняя ID
            const updatedUser = {
                id: currentUser.id,
                email: currentUser.email, // Email остается из токена
                name: userData.name // Имя обновляется из бэкенда
            };
            
            localStorage.setItem('user', JSON.stringify(updatedUser));
            setUser(updatedUser);
        } catch (error) {
            console.error('Error updating user data:', error);
            // В случае ошибки очищаем данные
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            setUser(null);
            setIsAuthenticated(false);
        }
    };

    const value = {
        user,
        isAuthenticated,
        isLoading,
        login,
        signup,
        logout,
        updateUserData
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}; 