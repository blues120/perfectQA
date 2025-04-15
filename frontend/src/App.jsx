import React from 'react'
import { Container, CssBaseline, ThemeProvider, createTheme } from '@mui/material'
import ChatInterface from './components/ChatInterface'

const theme = createTheme({
    palette: {
        mode: 'light',
        primary: {
            main: '#1976d2',
        },
        secondary: {
            main: '#dc004e',
        },
    },
})

function App() {
    return (
        <ThemeProvider theme={theme}>
            <CssBaseline />
            <Container>
                <ChatInterface />
            </Container>
        </ThemeProvider>
    )
}

export default App 