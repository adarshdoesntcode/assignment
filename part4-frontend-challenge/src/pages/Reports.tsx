import { useTransactionReports } from "@/hooks/useTransactionReports";
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card";
import { Spinner } from "@/components/ui/spinner";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  Legend,
  AreaChart,
  Area,
} from "recharts";
import {
  CreditCard,
  Activity,
  DollarSign,
  CheckCircle2,
} from "lucide-react";

const COLORS = ["#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#8884d8"];

export const Reports = () => {
  const { data, loading, error } = useTransactionReports();

  if (loading) {
    return (
      <div className="flex items-center justify-center h-[calc(100vh-200px)]">
        <Spinner />
      </div>
    );
  }

  if (error) {
    return (
      <div className="container px-4 py-8 mx-auto">
        <div className="p-8 text-center text-red-600 border border-red-200 rounded-lg bg-red-50">
          <p className="font-semibold">Error loading reports</p>
          <p className="mt-2 text-sm">{error.message}</p>
        </div>
      </div>
    );
  }

  if (!data) return null;

  const {
    volumeMetrics,
    successRateMetrics,
    amountTrends,
    peakTimesHeatmap,
    cardTypeDistribution,
    reportPeriod,
  } = data;

  // Format currency
  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat("en-US", {
      style: "currency",
      currency: "USD",
    }).format(amount);
  };

  // Format date
  // Format date
  const formatDate = (dateString: string) => {
    try {
      // Remove NPT or other timezones that might cause issues
      const cleanDate = dateString.replace(" NPT", "").replace(" IST", "");
      const date = new Date(cleanDate);

      if (isNaN(date.getTime())) {
        console.error("Invalid date:", dateString);
        return dateString;
      }

      return date.toLocaleDateString("en-US", {
        month: "short",
        day: "numeric",
      });
    } catch (e) {
      console.error("Error formatting date:", e);
      return dateString;
    }
  };

  console.log("Volume Metrics:", volumeMetrics);

  const cardTypeData = Object.entries(cardTypeDistribution.byType).map(
    ([name, value]) => ({
      name: name.charAt(0).toUpperCase() + name.slice(1),
      value,
    })
  );

  return (
    <main className="container px-4 py-8 mx-auto space-y-8">
      {/* Header Section */}
      <div className="flex flex-col gap-2 md:flex-row md:items-center md:justify-between">
        <div>
          <h1 className="text-3xl font-bold tracking-tight">Analytics Dashboard</h1>
          <p className="text-muted-foreground">
            Transaction overview from {formatDate(reportPeriod.start)} to{" "}
            {formatDate(reportPeriod.end)}
          </p>
        </div>
      </div>

      {/* Key Metrics Cards */}
      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Total Revenue</CardTitle>
            <DollarSign className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {formatCurrency(
                volumeMetrics.daily.reduce((acc, curr) => acc + curr.amount, 0)
              )}
            </div>
            <p className="text-xs text-muted-foreground">
              Across {successRateMetrics.totalTransactions.toLocaleString()} transactions
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Success Rate</CardTitle>
            <CheckCircle2 className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {successRateMetrics.successRate.toFixed(1)}%
            </div>
            <p className="text-xs text-muted-foreground">
              {successRateMetrics.completed.toLocaleString()} successful transactions
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Avg. Transaction</CardTitle>
            <Activity className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {formatCurrency(amountTrends.overall.average)}
            </div>
            <p className="text-xs text-muted-foreground">
              Median: {formatCurrency(amountTrends.overall.median)}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="flex flex-row items-center justify-between pb-2 space-y-0">
            <CardTitle className="text-sm font-medium">Active Cards</CardTitle>
            <CreditCard className="w-4 h-4 text-muted-foreground" />
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">
              {Object.keys(cardTypeDistribution.byType).length} Types
            </div>
            <p className="text-xs text-muted-foreground">
              Most used:{" "}
              {cardTypeData.sort((a, b) => b.value - a.value)[0]?.name}
            </p>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 lg:grid-cols-7">
        {/* Transaction Volume Chart */}
        <Card className="lg:col-span-4">
          <CardHeader>
            <CardTitle>Transaction Volume</CardTitle>
            <CardDescription>
              Daily transaction volume and revenue trends
            </CardDescription>
          </CardHeader>
          <CardContent className="pl-2">
            <Tabs defaultValue="daily" className="space-y-4">
              <TabsList>
                <TabsTrigger value="daily">Daily</TabsTrigger>
                <TabsTrigger value="weekly">Weekly</TabsTrigger>
                <TabsTrigger value="monthly">Monthly</TabsTrigger>
              </TabsList>
              <TabsContent value="daily">
                <div style={{ width: '100%', height: '300px' }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <AreaChart data={volumeMetrics.daily}>
                      <defs>
                        <linearGradient id="colorAmount" x1="0" y1="0" x2="0" y2="1">
                          <stop offset="5%" stopColor="#91abf8ff" stopOpacity={0.8} />
                          <stop offset="95%" stopColor="#91abf8ff" stopOpacity={0} />
                        </linearGradient>
                      </defs>
                      <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                      <XAxis
                        dataKey="date"
                        tickFormatter={(value) => formatDate(value)}
                        className="text-xs"
                      />
                      <YAxis
                        className="text-xs"
                        tickFormatter={(value) => `$${value / 1000}k`}
                      />
                      <Tooltip
                        labelFormatter={(value) => formatDate(value as string)}
                        formatter={(value: number) => [formatCurrency(value), "Amount"]}
                      />
                      <Area
                        type="monotone"
                        dataKey="amount"
                        stroke="#91abf8ff"
                        fillOpacity={1}
                        fill="url(#colorAmount)"
                      />
                    </AreaChart>
                  </ResponsiveContainer>
                </div>
              </TabsContent>
              <TabsContent value="weekly">
                <div style={{ width: '100%', height: '300px' }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={volumeMetrics.weekly}>
                      <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                      <XAxis
                        dataKey="weekStart"
                        tickFormatter={(value) => formatDate(value)}
                        className="text-xs"
                      />
                      <YAxis
                        className="text-xs"
                        tickFormatter={(value) => `$${value / 1000}k`}
                      />
                      <Tooltip
                        labelFormatter={(value) => `Week of ${formatDate(value as string)}`}
                        formatter={(value: number) => [formatCurrency(value), "Amount"]}
                      />
                      <Bar dataKey="amount" fill="#91abf8ff" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </TabsContent>
              <TabsContent value="monthly">
                <div style={{ width: '100%', height: '300px' }}>
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={volumeMetrics.monthly}>
                      <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                      <XAxis dataKey="month" className="text-xs" />
                      <YAxis
                        className="text-xs"
                        tickFormatter={(value) => `$${value / 1000}k`}
                      />
                      <Tooltip
                        formatter={(value: number) => [formatCurrency(value), "Amount"]}
                      />
                      <Bar dataKey="amount" fill="#91abf8ff" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </TabsContent>
            </Tabs>
          </CardContent>
        </Card>

        {/* Card Type Distribution */}
        <Card className="lg:col-span-3">
          <CardHeader>
            <CardTitle>Card Distribution</CardTitle>
            <CardDescription>Transaction volume by card type</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={cardTypeData}
                    cx="50%"
                    cy="50%"
                    innerRadius={60}
                    outerRadius={80}
                    paddingAngle={5}
                    dataKey="value"
                  >
                    {cardTypeData.map((_entry, index) => (
                      <Cell
                        key={`cell-${index}`}
                        fill={COLORS[index % COLORS.length]}
                      />
                    ))}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid gap-4 lg:grid-cols-7">
        {/* Peak Times Heatmap */}
        <Card className="lg:col-span-4">
          <CardHeader>
            <CardTitle>Peak Transaction Times</CardTitle>
            <CardDescription>
              Hourly transaction distribution (24h format)
            </CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart data={peakTimesHeatmap.hourly}>
                  <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                  <XAxis dataKey="hour" className="text-xs" />
                  <YAxis className="text-xs" />
                  <Tooltip
                    labelFormatter={(value) => `${value}:00 - ${value}:59`}
                    formatter={(value: number) => [value, "Transactions"]}
                  />
                  <Bar dataKey="count" fill="#82ca9d" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        {/* Success vs Failure */}
        <Card className="lg:col-span-3">
          <CardHeader>
            <CardTitle>Transaction Status</CardTitle>
            <CardDescription>Success vs Failure Rate Analysis</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="h-[300px]">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={[
                      { name: "Completed", value: successRateMetrics.completed },
                      { name: "Failed", value: successRateMetrics.failed },
                      { name: "Pending", value: successRateMetrics.byStatus.pending },
                    ]}
                    cx="50%"
                    cy="50%"
                    outerRadius={80}
                    dataKey="value"
                    label={({ name, percent }) =>
                      `${name} ${(percent * 100).toFixed(0)}%`
                    }
                  >
                    <Cell fill="#22c55e" /> {/* Green for Completed */}
                    <Cell fill="#ef4444" /> {/* Red for Failed */}
                    <Cell fill="#eab308" /> {/* Yellow for Pending */}
                  </Pie>
                  <Tooltip />
                  <Legend />
                </PieChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>
      </div>

      {/* Average Amount Trends */}
      <Card>
        <CardHeader>
          <CardTitle>Average Transaction Value Trends</CardTitle>
          <CardDescription>
            Daily average transaction amount over time
          </CardDescription>
        </CardHeader>
        <CardContent>
          <div className="h-[300px]">
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={amountTrends.daily}>
                <CartesianGrid strokeDasharray="3 3" className="stroke-muted" />
                <XAxis
                  dataKey="date"
                  tickFormatter={(value) => formatDate(value)}
                  className="text-xs"
                />
                <YAxis
                  className="text-xs"
                  tickFormatter={(value) => `$${(value / 1000).toFixed(1)}k`}
                />
                <Tooltip
                  labelFormatter={(value) => formatDate(value as string)}
                  formatter={(value: number) => [formatCurrency(value), "Avg Amount"]}
                />
                <Line
                  type="monotone"
                  dataKey="average"
                  stroke="#ff7300"
                  strokeWidth={2}
                  dot={{ r: 4 }}
                  activeDot={{ r: 8 }}
                />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </CardContent>
      </Card>
    </main >
  );
};
