import Request from '../request'

export interface MyTicket {
  id: number
  trainName: string
  fromStation: string
  toStation: string
  departureTime: string
  status: 'DEPARTED' | 'UPCOMING_SOON' | 'UPCOMING'
  statusText: string
}

class TicketService {
  myTickets = (): Promise<MyTicket[]> => Request.get('tickets/me')
}

export default new TicketService()
