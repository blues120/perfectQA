import request from '@/utils/request'

const baseUrl = '/api/agent'

export function getAgentList(params) {
  return request({
    url: `${baseUrl}/page`,
    method: 'get',
    params
  })
}

export function searchAgents(params) {
  return request({
    url: `${baseUrl}/search`,
    method: 'get',
    params
  })
}

export function getEnabledAgents() {
  return request({
    url: `${baseUrl}/enabled`,
    method: 'get'
  })
}

export function getAgentsByType(type) {
  return request({
    url: `${baseUrl}/type/${type}`,
    method: 'get'
  })
}

export function getAgentsByUser(createUser) {
  return request({
    url: `${baseUrl}/user/${createUser}`,
    method: 'get'
  })
}

export function getAgentById(id) {
  return request({
    url: `${baseUrl}/${id}`,
    method: 'get'
  })
}

export function createAgent(data) {
  return request({
    url: baseUrl,
    method: 'post',
    data
  })
}

export function updateAgent(id, data) {
  return request({
    url: `${baseUrl}/${id}`,
    method: 'put',
    data
  })
}

export function updateAgentStatus(id, status) {
  return request({
    url: `${baseUrl}/${id}/status`,
    method: 'put',
    params: { status }
  })
}

export function toggleAgentEnabled(id, isEnabled) {
  return request({
    url: `${baseUrl}/${id}/enabled`,
    method: 'put',
    params: { isEnabled }
  })
}

export function executeAgent(id, params) {
  return request({
    url: `${baseUrl}/${id}/execute`,
    method: 'post',
    data: params
  })
}

export function deleteAgent(id) {
  return request({
    url: `${baseUrl}/${id}`,
    method: 'delete'
  })
} 