import React, { useState, useRef, useEffect } from 'react'
import axios from 'axios'
import { Box, TextField, Button, Paper, Typography, List, ListItem, ListItemText, Select, MenuItem, FormControl, InputLabel } from '@mui/material'

const ChatInterface = () => {
    const [messages, setMessages] = useState([])
    const [input, setInput] = useState('')
    const [instructions, setInstructions] = useState([])
    const [selectedInstruction, setSelectedInstruction] = useState('')
    const messagesEndRef = useRef(null)

    useEffect(() => {
        fetchInstructions()
    }, [])

    useEffect(() => {
        scrollToBottom()
    }, [messages])

    const fetchInstructions = async () => {
        try {
            const response = await axios.get('/api/instructions')
            setInstructions(response.data)
        } catch (error) {
            console.error('Error fetching instructions:', error)
        }
    }

    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: "smooth" })
    }

    const handleSend = async () => {
        if (!input.trim()) return

        const userMessage = { content: input, type: 'user' }
        setMessages(prev => [...prev, userMessage])
        setInput('')

        try {
            const response = await axios.post('/api/chat', input)
            const aiMessage = { content: response.data, type: 'assistant' }
            setMessages(prev => [...prev, aiMessage])
        } catch (error) {
            console.error('Error sending message:', error)
        }
    }

    const handleClear = async () => {
        try {
            await axios.post('/api/chat/clear')
            setMessages([])
        } catch (error) {
            console.error('Error clearing chat:', error)
        }
    }

    const handleInstructionChange = async (event) => {
        const instructionId = event.target.value
        setSelectedInstruction(instructionId)
        try {
            await axios.post(`/api/chat/instruction/${instructionId}`)
            setMessages([])
        } catch (error) {
            console.error('Error setting instruction:', error)
        }
    }

    return (
        <Box sx={{ maxWidth: 800, margin: 'auto', p: 2 }}>
            <Paper elevation={3} sx={{ p: 2, mb: 2 }}>
                <FormControl fullWidth sx={{ mb: 2 }}>
                    <InputLabel>Select Instruction</InputLabel>
                    <Select
                        value={selectedInstruction}
                        onChange={handleInstructionChange}
                        label="Select Instruction"
                    >
                        <MenuItem value="">
                            <em>None</em>
                        </MenuItem>
                        {instructions.map(instruction => (
                            <MenuItem key={instruction.id} value={instruction.id}>
                                {instruction.name}
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>

                <Box sx={{ height: 400, overflow: 'auto', mb: 2, p: 2, bgcolor: 'background.paper' }}>
                    <List>
                        {messages.map((message, index) => (
                            <ListItem key={index} sx={{
                                justifyContent: message.type === 'user' ? 'flex-end' : 'flex-start',
                                mb: 1
                            }}>
                                <Paper
                                    elevation={1}
                                    sx={{
                                        p: 2,
                                        maxWidth: '70%',
                                        bgcolor: message.type === 'user' ? 'primary.light' : 'secondary.light',
                                        color: message.type === 'user' ? 'primary.contrastText' : 'secondary.contrastText'
                                    }}
                                >
                                    <Typography>{message.content}</Typography>
                                </Paper>
                            </ListItem>
                        ))}
                        <div ref={messagesEndRef} />
                    </List>
                </Box>

                <Box sx={{ display: 'flex', gap: 1 }}>
                    <TextField
                        fullWidth
                        variant="outlined"
                        value={input}
                        onChange={(e) => setInput(e.target.value)}
                        onKeyPress={(e) => e.key === 'Enter' && handleSend()}
                        placeholder="Type your message..."
                    />
                    <Button variant="contained" onClick={handleSend}>
                        Send
                    </Button>
                    <Button variant="outlined" onClick={handleClear}>
                        Clear
                    </Button>
                </Box>
            </Paper>
        </Box>
    )
}

export default ChatInterface 