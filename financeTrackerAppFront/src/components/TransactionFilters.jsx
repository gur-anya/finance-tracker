import { useState } from 'react';
import { Form, Button, Row, Col, Card } from 'react-bootstrap';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { getAllCategories, getCategoryDisplayName } from '../utils/categoryMapper';

function TransactionFilters({ onFiltersChange, onPageSizeChange, pageSize }) {
    const [filters, setFilters] = useState({
        sumMoreThan: '',
        sumLessThan: '',
        category: '',
        type: '',
        startDate: null,
        endDate: null
    });

    const categories = getAllCategories();

    const handleFilterChange = (field, value) => {
        const newFilters = { ...filters, [field]: value };
        setFilters(newFilters);
        onFiltersChange(newFilters);
    };

    const handlePageSizeChange = (newPageSize) => {
        onPageSizeChange(newPageSize);
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
        setFilters(resetFilters);
        onFiltersChange(resetFilters);
    };

    const hasActiveFilters = () => {
        return filters.sumMoreThan || filters.sumLessThan || 
               filters.category || filters.type || 
               filters.startDate || filters.endDate;
    };

    return (
        <Card className="mb-3">
            <Card.Header>
                <div className="d-flex justify-content-between align-items-center">
                    <h6 className="mb-0">Фильтры и пагинация</h6>
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
            </Card.Header>
            <Card.Body>
                <Row>
                    {/* Сумма больше */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Сумма больше</Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="0.00"
                                value={filters.sumMoreThan}
                                onChange={(e) => handleFilterChange('sumMoreThan', e.target.value)}
                                step="0.01"
                                min="0"
                            />
                        </Form.Group>
                    </Col>

                    {/* Сумма меньше */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Сумма меньше</Form.Label>
                            <Form.Control
                                type="number"
                                placeholder="0.00"
                                value={filters.sumLessThan}
                                onChange={(e) => handleFilterChange('sumLessThan', e.target.value)}
                                step="0.01"
                                min="0"
                            />
                        </Form.Group>
                    </Col>

                    {/* Категория */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Категория</Form.Label>
                            <Form.Select
                                value={filters.category}
                                onChange={(e) => handleFilterChange('category', e.target.value)}
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

                    {/* Тип */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Тип</Form.Label>
                            <Form.Select
                                value={filters.type}
                                onChange={(e) => handleFilterChange('type', e.target.value)}
                            >
                                <option value="">Все типы</option>
                                <option value="INCOME">Доход</option>
                                <option value="EXPENSE">Расход</option>
                            </Form.Select>
                        </Form.Group>
                    </Col>
                </Row>

                <Row>
                    {/* Период - начальная дата */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Начальная дата</Form.Label>
                            <DatePicker
                                selected={filters.startDate}
                                onChange={(date) => handleFilterChange('startDate', date)}
                                selectsStart
                                startDate={filters.startDate}
                                endDate={filters.endDate}
                                className="form-control"
                                placeholderText="Выберите дату"
                                dateFormat="dd.MM.yyyy"
                                isClearable
                            />
                        </Form.Group>
                    </Col>

                    {/* Период - конечная дата */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Конечная дата</Form.Label>
                            <DatePicker
                                selected={filters.endDate}
                                onChange={(date) => handleFilterChange('endDate', date)}
                                selectsEnd
                                startDate={filters.startDate}
                                endDate={filters.endDate}
                                minDate={filters.startDate}
                                className="form-control"
                                placeholderText="Выберите дату"
                                dateFormat="dd.MM.yyyy"
                                isClearable
                            />
                        </Form.Group>
                    </Col>

                    {/* Пагинация */}
                    <Col md={3} className="mb-2">
                        <Form.Group>
                            <Form.Label>Показать транзакций</Form.Label>
                            <Form.Select
                                value={pageSize}
                                onChange={(e) => handlePageSizeChange(parseInt(e.target.value))}
                            >
                                <option value={10}>10</option>
                                <option value={50}>50</option>
                                <option value={100}>100</option>
                            </Form.Select>
                        </Form.Group>
                    </Col>

                    <Col md={3} className="mb-2 d-flex align-items-end">
                        <div className="w-100">
                            <small className="text-muted">
                                {hasActiveFilters() ? 'Фильтры активны' : 'Фильтры не применены'}
                            </small>
                        </div>
                    </Col>
                </Row>
            </Card.Body>
        </Card>
    );
}

export default TransactionFilters; 