import { useState } from 'react';
import { Nav, Tab, Container } from 'react-bootstrap';
import Goals from './Goals.jsx';

function MainTabs({transactionsContent, statsContent, goalsContent}) {
    const [activeTab, setActiveTab] = useState('transactions');

    return (
        <div className="tabs-container">
            <Container fluid>
                <Nav variant="tabs" className="mb-4" activeKey={activeTab} onSelect={(k) => setActiveTab(k)}>
                    <Nav.Item className="flex-fill">
                        <Nav.Link eventKey="transactions" className="text-center">
                            ТРАНЗАКЦИИ
                        </Nav.Link>
                    </Nav.Item>
                    <Nav.Item className="flex-fill">
                        <Nav.Link eventKey="stats" className="text-center">
                            СТАТИСТИКА
                        </Nav.Link>
                    </Nav.Item>
                    <Nav.Item className="flex-fill">
                        <Nav.Link eventKey="goals" className="text-center">
                            ЦЕЛЬ
                        </Nav.Link>
                    </Nav.Item>
                </Nav>
                
                <Tab.Content>
                    <Tab.Pane active={activeTab === 'transactions'}>
                        <div className="transactions-container">
                            {transactionsContent}
                        </div>
                    </Tab.Pane>
                    <Tab.Pane active={activeTab === 'stats'}>
                        <div className="stats-container">
                            {statsContent}
                        </div>
                    </Tab.Pane>
                    <Tab.Pane active={activeTab === 'goals'}>
                        <div className="stats-container">
                            {goalsContent}
                        </div>
                    </Tab.Pane>
                </Tab.Content>
            </Container>
        </div>
    );
}

export default MainTabs;