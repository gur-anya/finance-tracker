// Маппинг английских категорий с бэкенда на русские названия для отображения
export const categoryMapping = {
    // Расходы (EXPENSE)
    'FOOD': 'ЕДА',
    'TRANSPORT': 'ТРАНСПОРТ',
    'ENTERTAINMENT': 'РАЗВЛЕЧЕНИЯ',
    'SHOPPING': 'ПОКУПКИ',
    'HEALTH': 'ЗДОРОВЬЕ',
    'EDUCATION': 'ОБРАЗОВАНИЕ',
    'HOME': 'ДОМ',
    'OTHER': 'ДРУГОЕ',
    
    // Доходы (INCOME)
    'WAGE': 'ЗАРПЛАТА',
    'FREELANCE': 'ФРИЛАНС',
    'GIFT': 'ПОДАРКИ',
    'INVESTMENT': 'ИНВЕСТИЦИИ',
    'SALES': 'ПРОДАЖИ',
    
    // Специальные
    'GOAL': 'ЦЕЛЬ'
};

// Обратный маппинг для отправки на бэкенд
export const reverseCategoryMapping = Object.fromEntries(
    Object.entries(categoryMapping).map(([key, value]) => [value, key])
);

// Функция для получения русского названия категории
export const getCategoryDisplayName = (englishCategory) => {
    return categoryMapping[englishCategory] || englishCategory;
};

// Функция для получения английского названия категории
export const getCategoryEnglishName = (russianCategory) => {
    return reverseCategoryMapping[russianCategory] || russianCategory;
};

// Получение списка категорий для расходов
export const getExpenseCategories = () => {
    return [
        'FOOD', 'TRANSPORT', 'ENTERTAINMENT', 'SHOPPING', 
        'HEALTH', 'EDUCATION', 'HOME', 'OTHER'
    ];
};

// Получение списка категорий для доходов
export const getIncomeCategories = () => {
    return [
        'WAGE', 'FREELANCE', 'GIFT', 'INVESTMENT', 'SALES', 'OTHER'
    ];
};

// Получение всех категорий
export const getAllCategories = () => {
    return Object.keys(categoryMapping);
};

// Получение русских названий для отображения
export const getRussianCategories = () => {
    return Object.values(categoryMapping);
}; 