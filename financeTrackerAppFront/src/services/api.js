const API_BASE_URL = '/api/v1';

class ApiService {
    constructor() {
        this.baseURL = API_BASE_URL;
    }

    // Получить токен из localStorage
    getToken() {
        return localStorage.getItem('token');
    }

    // Создать заголовки с авторизацией
    getHeaders() {
        const token = this.getToken();
        return {
            'Content-Type': 'application/json',
            ...(token && { 'Authorization': `Bearer ${token}` })
        };
    }

    // Обработка ответа
    async handleResponse(response) {
        if (!response.ok) {
            let errorMessage = `HTTP error! status: ${response.status}`;
            
            try {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || errorMessage;
            } catch (e) {
                // Если не удалось распарсить JSON, используем стандартное сообщение
            }
            
            // Специальная обработка для ошибки 409 (EmailAlreadyExistsException)
            if (response.status === 409) {
                throw new Error(errorMessage || 'Пользователь с таким email уже существует');
            }
            
            throw new Error(errorMessage);
        }
        
        // Проверяем, есть ли тело ответа
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            const data = await response.json();
            console.log('Response data:', data);
            return data;
        } else {
            // Для ответов без тела (например, DELETE)
            return { success: true };
        }
    }

    // Аутентификация
    async login(credentials) {
        const response = await fetch(`${this.baseURL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(credentials)
        });
        return this.handleResponse(response);
    }

    async signup(userData) {
        const response = await fetch(`${this.baseURL}/auth/signup`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userData)
        });
        return this.handleResponse(response);
    }

    async logout() {
        const response = await fetch(`${this.baseURL}/auth/logout`, {
            method: 'POST',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    // Транзакции
    async getAllTransactions() {
        const response = await fetch(`${this.baseURL}/transactions`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        const result = await this.handleResponse(response);
        console.log('All transactions from backend:', result);
        // API возвращает объект с полем transactions, а не массив напрямую
        return result.transactions || [];
    }

    async createTransaction(transactionData) {
        const user = JSON.parse(localStorage.getItem('user') || '{}');
        const token = localStorage.getItem('token');
        
        if (!token) {
            throw new Error('Токен авторизации не найден');
        }
        
        if (!user.id) {
            throw new Error('ID пользователя не найден. Попробуйте перезайти в систему.');
        }
        
        // Преобразуем тип транзакции в enum
        const type = transactionData.type === 1 ? 'INCOME' : 'EXPENSE';
        
        const requestData = {
            type: type,
            sum: transactionData.sum,
            category: transactionData.category,
            description: transactionData.description
        };
        
        console.log('Creating transaction for user ID:', user.id);
        console.log('Request data:', requestData);
        
        try {
            const response = await fetch(`${this.baseURL}/transactions/${user.id}`, {
                method: 'POST',
                headers: this.getHeaders(),
                body: JSON.stringify(requestData)
            });
            return this.handleResponse(response);
        } catch (error) {
            if (error.message.includes('ERR_CONNECTION_REFUSED')) {
                throw new Error('Сервер недоступен. Проверьте, что бэкенд запущен на порту 8080.');
            }
            throw error;
        }
    }

    async updateTransaction(transactionId, transactionData) {
        // Преобразуем тип транзакции в enum
        const type = transactionData.type === 1 ? 'INCOME' : 'EXPENSE';
        
        const requestData = {
            type: type,
            sum: transactionData.sum,
            category: transactionData.category,
            description: transactionData.description
        };
        
        const response = await fetch(`${this.baseURL}/transactions/${transactionId}`, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify(requestData)
        });
        return this.handleResponse(response);
    }

    async deleteTransaction(transactionId) {
        const response = await fetch(`${this.baseURL}/transactions/${transactionId}`, {
            method: 'DELETE',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    // Фильтрация транзакций (фейковый эндпоинт)
    async filterTransactions(filters) {
        // TODO: Заменить на реальный эндпоинт когда будет готов
        console.log('Фильтрация транзакций:', filters);
        // Временно возвращаем все транзакции
        return this.getAllTransactions();
    }

    // Цели
    async getUserGoal() {
        const response = await fetch(`${this.baseURL}/goal`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    // Получить накопления по цели через новый эндпоинт
    async getGoalSavings() {
        const response = await fetch(`${this.baseURL}/goal/saved`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        // Ожидаем DTO: { gotToGoal: number }
        const data = await this.handleResponse(response);
        return { savedAmount: data.gotToGoal || 0 };
    }

    async setUserGoal(goalData) {
        // Структура с названием и суммой согласно GoalRequestDTO
        const requestData = {
            goalName: goalData.title,
            goalSum: goalData.targetAmount
        };
        
        const response = await fetch(`${this.baseURL}/goal/set`, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify(requestData)
        });
        return this.handleResponse(response);
    }

    async updateUserGoal(goalData) {
        // Обновление существующей цели согласно GoalRequestDTO
        const requestData = {
            goalName: goalData.title,
            goalSum: goalData.targetAmount
        };
        
        const response = await fetch(`${this.baseURL}/goal/update`, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify(requestData)
        });
        return this.handleResponse(response);
    }

    async resetUserGoal() {
        const response = await fetch(`${this.baseURL}/goal/reset`, {
            method: 'PATCH',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    async clearGoalTransactions() {
        const response = await fetch(`${this.baseURL}/goal`, {
            method: 'DELETE',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    // Статистика
    async getBalanceForPeriod(periodData) {
        // Spring ожидает отдельные параметры, а не объект PeriodDTO
        const queryParams = new URLSearchParams({
            'startTimestamp': periodData.startDate,
            'endTimestamp': periodData.endDate
        });
        
        console.log('Balance API call with params:', queryParams.toString());
        console.log('Period data:', periodData);
        
        const response = await fetch(`${this.baseURL}/transactionStatistics/balance?${queryParams}`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        
        console.log('Balance response status:', response.status);
        const result = await this.handleResponse(response);
        console.log('Balance response data:', result);
        return result;
    }

    async getIncomesForPeriod(periodData) {
        const queryParams = new URLSearchParams({
            'startTimestamp': periodData.startDate,
            'endTimestamp': periodData.endDate
        });
        
        const response = await fetch(`${this.baseURL}/transactionStatistics/incomes?${queryParams}`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        const result = await this.handleResponse(response);
        // API возвращает поле incomes, а не totalIncome
        return { totalIncome: result.incomes || 0 };
    }

    async getExpensesForPeriod(periodData) {
        const queryParams = new URLSearchParams({
            'startTimestamp': periodData.startDate,
            'endTimestamp': periodData.endDate
        });
        
        const response = await fetch(`${this.baseURL}/transactionStatistics/expenses?${queryParams}`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        const result = await this.handleResponse(response);
        // API возвращает поле expenses, а не totalExpense
        return { totalExpense: result.expenses || 0 };
    }

    async getReportForPeriod(periodData) {
        const queryParams = new URLSearchParams({
            'startTimestamp': periodData.startDate,
            'endTimestamp': periodData.endDate
        });
        
        const response = await fetch(`${this.baseURL}/transactionStatistics/report?${queryParams}`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    // Пользователи - получаем данные из токена JWT
    async getCurrentUser() {
        const token = this.getToken();
        if (!token) {
            throw new Error('Токен не найден');
        }
        
        // Декодируем JWT токен для получения email
        try {
            const payload = JSON.parse(atob(token.split('.')[1]));
            const email = payload.sub; // subject содержит email
            
            // Возвращаем данные пользователя из токена
            return {
                id: null, // ID нужно получать из localStorage
                email: email,
                name: email.split('@')[0] // Временно используем email как имя
            };
        } catch (error) {
            throw new Error('Ошибка декодирования токена');
        }
    }

    // Получить данные пользователя по ID
    async getUserById(userId) {
        const response = await fetch(`${this.baseURL}/users/${userId}`, {
            method: 'GET',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }

    async updateUser(userId, userData) {
        const response = await fetch(`${this.baseURL}/users/${userId}`, {
            method: 'PATCH',
            headers: this.getHeaders(),
            body: JSON.stringify(userData)
        });
        return this.handleResponse(response);
    }

    async deleteUser(userId) {
        const response = await fetch(`${this.baseURL}/users/${userId}`, {
            method: 'DELETE',
            headers: this.getHeaders()
        });
        return this.handleResponse(response);
    }
}

export default new ApiService();