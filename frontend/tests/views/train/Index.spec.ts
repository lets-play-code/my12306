import { cleanup, render, screen } from '@testing-library/vue';
import ElementPlus from 'element-plus';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { nextTick } from 'vue';
import TrainIndex from '../../../src/views/train/Index.vue';
import {
  PURCHASED_TICKETS_KEY,
  getUpcomingPurchasedTickets,
  normalizeDepartureTime,
  savePurchasedTicket
} from '../../../src/views/train/purchaseReminder';

vi.mock('@/main', () => ({
  showMessage: vi.fn()
}));

vi.mock('@/api/index', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn()
  }
}));

describe('Train purchase reminders', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-03-21T08:00:00Z'));
  });

  afterEach(() => {
    cleanup();
    vi.useRealTimers();
  });

  it('returns only purchased trains departing within 3 hours', () => {
    savePurchasedTicket({
      trainId: 1,
      trainName: 'G102',
      description: 'G102 北京南-上海虹桥',
      departureTime: '2026-03-21T09:30:00'
    });
    savePurchasedTicket({
      trainId: 2,
      trainName: 'G103',
      description: 'G103 上海虹桥-北京南',
      departureTime: '2026-03-21T12:30:00'
    });

    const reminders = getUpcomingPurchasedTickets();

    expect(reminders).toHaveLength(1);
    expect(reminders[0].description).toBe('G102 北京南-上海虹桥');
  });

  it('deduplicates the same purchased reminder when saving', () => {
    const ticket = {
      trainId: 1,
      trainName: 'G102',
      description: 'G102 北京南-上海虹桥',
      departureTime: '2026-03-21T09:30:00'
    };

    savePurchasedTicket(ticket);
    savePurchasedTicket(ticket);

    const storedTickets = JSON.parse(localStorage.getItem(PURCHASED_TICKETS_KEY) ?? '[]');
    expect(storedTickets).toHaveLength(1);
  });

  it('normalizes backend UTC time strings without timezone suffix', () => {
    expect(normalizeDepartureTime('2026-03-21T17:40:35')).toBe('2026-03-21T17:40:35Z');
    expect(normalizeDepartureTime('2026-03-21T17:40:35Z')).toBe('2026-03-21T17:40:35Z');
  });

  it('shows a red warning when an existing purchased train is about to depart', async () => {
    localStorage.setItem(
      PURCHASED_TICKETS_KEY,
      JSON.stringify([
        {
          trainId: 1,
          trainName: 'G102',
          description: 'G102 北京南-上海虹桥',
          departureTime: '2026-03-21T09:30:00'
        }
      ])
    );

    render(TrainIndex, {
      global: {
        plugins: [ElementPlus]
      }
    });
    await nextTick();

    const warning = screen.getByTestId('departure-warning');
    expect(warning.className).toContain('border-red-200');
    expect(warning.className).toContain('bg-red-50');
    expect(warning.textContent).toContain('发车提醒');
    expect(warning.textContent).toContain('G102 北京南-上海虹桥');
  });
});
