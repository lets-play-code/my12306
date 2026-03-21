export const PURCHASED_TICKETS_KEY = 'my12306.purchasedTickets';
const THREE_HOURS_IN_MS = 3 * 60 * 60 * 1000;

export interface PurchasedTicketReminder {
  trainId: number;
  trainName: string;
  description: string;
  departureTime?: string;
}

const parseStoredTickets = (value: string | null): PurchasedTicketReminder[] => {
  if (!value) {
    return [];
  }

  try {
    const parsed = JSON.parse(value);
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
};

export const normalizeDepartureTime = (departureTime?: string) => {
  if (!departureTime) {
    return undefined;
  }

  if (/([zZ]|[+-]\d{2}:\d{2})$/.test(departureTime)) {
    return departureTime;
  }

  return `${departureTime}Z`;
};

const getDepartureTimestamp = (departureTime?: string) => {
  const normalizedDepartureTime = normalizeDepartureTime(departureTime);
  if (!normalizedDepartureTime) {
    return Number.NaN;
  }

  return new Date(normalizedDepartureTime).getTime();
};

const buildReminderKey = (ticket: PurchasedTicketReminder) => {
  return `${ticket.trainId}-${ticket.description}-${ticket.departureTime ?? ''}`;
};

export const getStoredPurchasedTickets = (): PurchasedTicketReminder[] => {
  return parseStoredTickets(localStorage.getItem(PURCHASED_TICKETS_KEY));
};

export const savePurchasedTicket = (ticket: PurchasedTicketReminder) => {
  const now = Date.now();
  const departureTimestamp = getDepartureTimestamp(ticket.departureTime);
  if (Number.isNaN(departureTimestamp) || departureTimestamp <= now) {
    return;
  }

  const currentTickets = getStoredPurchasedTickets().filter((item) => {
    const currentDepartureTimestamp = getDepartureTimestamp(item.departureTime);
    return !Number.isNaN(currentDepartureTimestamp) && currentDepartureTimestamp > now;
  });

  const nextTickets = [ticket, ...currentTickets];
  const uniqueTickets = nextTickets.filter((item, index, source) => {
    return source.findIndex((candidate) => buildReminderKey(candidate) === buildReminderKey(item)) === index;
  });

  localStorage.setItem(PURCHASED_TICKETS_KEY, JSON.stringify(uniqueTickets));
};

export const getUpcomingPurchasedTickets = (now = Date.now()): PurchasedTicketReminder[] => {
  return getStoredPurchasedTickets()
    .filter((ticket) => {
      const departureTimestamp = getDepartureTimestamp(ticket.departureTime);
      if (Number.isNaN(departureTimestamp)) {
        return false;
      }

      const timeDiff = departureTimestamp - now;
      return timeDiff > 0 && timeDiff <= THREE_HOURS_IN_MS;
    })
    .sort((left, right) => getDepartureTimestamp(left.departureTime) - getDepartureTimestamp(right.departureTime));
};

export const formatDepartureTime = (departureTime?: string) => {
  const normalizedDepartureTime = normalizeDepartureTime(departureTime);
  if (!normalizedDepartureTime) {
    return '待定';
  }

  const date = new Date(normalizedDepartureTime);
  if (Number.isNaN(date.getTime())) {
    return '待定';
  }

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  });
};
