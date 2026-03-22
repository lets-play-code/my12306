import Request from '../request'

export interface TicketResponse {
    id: number
    trainName: string
    departureTime: string
    travelDate: string
    fromStation: string
    toStation: string
}

export interface UpcomingTicketResponse extends TicketResponse {
    remainingMinutes: number
}

class Tickets {
    getMyTickets = (): Promise<TicketResponse[]> => {
        return Request.get('/tickets/my')
    }

    getUpcomingTickets = (): Promise<UpcomingTicketResponse[]> => {
        return Request.get('/tickets/upcoming')
    }
}

export default new Tickets()
