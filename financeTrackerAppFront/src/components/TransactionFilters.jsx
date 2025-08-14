import { useState, useEffect } from 'react';
import { Form, Button, Row, Col, Card, Collapse } from 'react-bootstrap';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { getAllCategories, getCategoryDisplayName } from '../utils/categoryMapper';

function TransactionFilters({ onFiltersChange, appliedFilters = {} }) {
    const [showFilters, setShowFilters] = useState(false);
    const [localFilters, setLocalFilters] = useState({
        sumMoreThan: appliedFilters.sumMoreThan || '',
        sumLessThan: appliedFilters.sumLessThan || '',
        category: appliedFilters.category || '',
        type: appliedFilters.type || '',
        startDate: appliedFilters.startDate || null,
        endDate: appliedFilters.endDate || null
    });

    const categories = getAllCategories();

    useEffect(() => {
        setLocalFilters({
            sumMoreThan: appliedFilters.sumMoreThan || '',
            sumLessThan: appliedFilters.sumLessThan || '',
            category: appliedFilters.category || '',
            type: appliedFilters.type || '',
            startDate: appliedFilters.startDate || null,
            endDate: appliedFilters.endDate || null
        });
    }, [appliedFilters]);

    const handleLocalFilterChange = (field, value) => {
        const newLocalFilters = { ...localFilters, [field]: value };
        setLocalFilters(newLocalFilters);
    };

    const applyFilters = () => {
        onFiltersChange(localFilters);
    };



    const resetFilters = () => {
        const resetFilters = {
            sumMoreThan: '',
            sumLessThan: '',
            category: '',
            type: '',
            startDate: null,
            endDate: null
        };
        setLocalFilters(resetFilters);
        onFiltersChange(resetFilters);
    };

    const hasActiveFilters = () => {
        return appliedFilters.sumMoreThan || appliedFilters.sumLessThan || 
               appliedFilters.category || appliedFilters.type || 
               appliedFilters.startDate || appliedFilters.endDate;
    };

    return (
        <Card className="mb-3">
            <Card.Body>
                <div className="d-flex justify-content-between align-items-center mb-3">
                    <Button 
                        variant="outline-primary" 
                        onClick={() => setShowFilters(!showFilters)}
                        aria-controls="filters-collapse"
                        aria-expanded={showFilters}
                    >
                        {showFilters ? 'Скрыть фильтры' : 'Фильтры'}
                    </Button>
                    
                    <div className="d-flex gap-2">
                        {hasActiveFilters() && (
                            <Button 
                                variant="outline-danger" 
                                size="sm" 
                                onClick={resetFilters}
                            >
                                Сбросить фильтры
                            </Button>
                        )}
                    </div>
                </div>

                <Collapse in={showFilters}>
                    <div id="filters-collapse">
                        <Row>
                            <Col md={3} className="mb-2">
                                <Form.Group>
                                    <Form.Label>Сумма больше</Form.Label>
                                    <Form.Control
                                        type="number"
                                        placeholder="0.00"
                                        value={localFilters.sumMoreThan}
                                        onChange={(e) => handleLocalFilterChange('sumMoreThan', e.target.value)}
                                        step="0.01"
                                        min="0"
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={3} className="mb-2">
                                <Form.Group>
                                    <Form.Label>Сумма меньше</Form.Label>
                                    <Form.Control
                                        type="number"
                                        placeholder="0.00"
                                        value={localFilters.sumLessThan}
                                        onChange={(e) => handleLocalFilterChange('sumLessThan', e.target.value)}
                                        step="0.01"
                                        min="0"
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={3} className="mb-2">
                                <Form.Group>
                                    <Form.Label>Категория</Form.Label>
                                    <Form.Select
                                        value={localFilters.category}
                                        onChange={(e) => handleLocalFilterChange('category', e.target.value)}
                                    >
                                        <option value="">Все категории</option>
                                        {categories.map(category => (
                                            <option key={category} value={category}>
                                                {getCategoryDisplayName(category)}
                                            </option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>

                            <Col md={3} className="mb-2">
                                <Form.Group>
                                    <Form.Label>Тип</Form.Label>
                                    <Form.Select
                                        value={localFilters.type}
                                        onChange={(e) => handleLocalFilterChange('type', e.target.value)}
                                    >
                                        <option value="">Все типы</option>
                                        <option value="INCOME">Доход</option>
                                        <option value="EXPENSE">Расход</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Row>
                            <Col md={3} className="mb-2">
                                <Form.Group>
                                    <Form.Label>Начальная дата</Form.Label>
                                    <DatePicker
                                        selected={localFilters.startDate}
                                        onChange={(date) => handleLocalFilterChange('startDate', date)}
                                        selectsStart
                                        startDate={localFilters.startDate}
                                        endDate={localFilters.endDate}
                                        className="form-control"
                                        placeholderText="Выберите дату"
                                        dateFormat="dd.MM.yyyy"
                                        isClearable
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={3} className="mb-2">
                                <Form.Group>
                                    <Form.Label>Конечная дата</Form.Label>
                                    <DatePicker
                                        selected={localFilters.endDate}
                                        onChange={(date) => handleLocalFilterChange('endDate', date)}
                                        selectsEnd
                                        startDate={localFilters.startDate}
                                        endDate={localFilters.endDate}
                                        minDate={localFilters.startDate}
                                        className="form-control"
                                        placeholderText="Выберите дату"
                                        dateFormat="dd.MM.yyyy"
                                        isClearable
                                    />
                                </Form.Group>
                            </Col>

                            <Col md={6} className="mb-2 d-flex align-items-end">
                                <Button 
                                    variant="primary" 
                                    onClick={applyFilters}
                                    className="w-100"
                                >
                                    Применить фильтры
                                </Button>
                            </Col>
                        </Row>
                    </div>
                </Collapse>
            </Card.Body>
        </Card>
    );
}

export default TransactionFilters; 