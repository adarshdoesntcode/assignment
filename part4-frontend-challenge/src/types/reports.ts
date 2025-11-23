export interface ReportPeriod {
    start: string;
    end: string;
}

export interface DailyVolume {
    date: string;
    count: number;
    amount: number;
}

export interface WeeklyVolume {
    weekStart: string;
    weekNumber: number;
    count: number;
    amount: number;
}

export interface MonthlyVolume {
    month: string;
    count: number;
    amount: number;
}

export interface VolumeMetrics {
    daily: DailyVolume[];
    weekly: WeeklyVolume[];
    monthly: MonthlyVolume[];
}

export interface SuccessRateMetrics {
    totalTransactions: number;
    completed: number;
    failed: number;
    successRate: number;
    failureRate: number;
    byStatus: {
        pending: number;
        failed: number;
        completed: number;
    };
}

export interface AmountStats {
    average: number;
    median: number;
    min: number;
    max: number;
}

export interface DailyAverageAmount {
    date: string;
    average: number;
}

export interface AmountTrends {
    overall: AmountStats;
    daily: DailyAverageAmount[];
}

export interface HourlyPeak {
    hour: number;
    count: number;
}

export interface DayOfWeekPeak {
    dayOfWeek: string;
    count: number;
}

export interface PeakTimesHeatmap {
    hourly: HourlyPeak[];
    dayOfWeek: DayOfWeekPeak[];
}

export interface CardTypeDistribution {
    byType: {
        [key: string]: number;
    };
    percentages: {
        [key: string]: number;
    };
}

export interface TransactionReportsData {
    reportPeriod: ReportPeriod;
    volumeMetrics: VolumeMetrics;
    successRateMetrics: SuccessRateMetrics;
    amountTrends: AmountTrends;
    peakTimesHeatmap: PeakTimesHeatmap;
    cardTypeDistribution: CardTypeDistribution;
}

export interface TransactionReportsResponse {
    status: number;
    success: boolean;
    message: string;
    data: TransactionReportsData;
}
