import { useState } from "react";
import { Button } from "@/components/ui/button";
import {
    AlertDialog,
    AlertDialogAction,
    AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
    AlertDialogTrigger,
} from "@/components/ui/alert-dialog";
import { Download, Loader2 } from "lucide-react";
import { toast } from "sonner";
import * as XLSX from "xlsx";
import { Transaction } from "@/types/transaction";

interface ExportTransactionsProps {
    merchantId: string;
}

export const ExportTransactions = ({ merchantId }: ExportTransactionsProps) => {
    const [isExporting, setIsExporting] = useState(false);
    const [open, setOpen] = useState(false);

    const handleExport = async () => {
        setIsExporting(true);

        try {
            // Fetch all transactions without pagination
            const response = await fetch(
                `/api/v1/transactions/${merchantId}?page=0&size=10000`
            );

            if (!response.ok) {
                throw new Error("Failed to fetch transactions");
            }

            const result = await response.json();
            const transactions: Transaction[] = result.data.transactions || [];

            if (transactions.length === 0) {
                toast.warning("No transactions to export");
                setOpen(false);
                return;
            }

            // Transform transactions data for Excel
            const exportData = transactions.map((txn) => ({
                "Transaction ID": txn.txnId,
                "Transaction Date": new Date(txn.txnDate).toLocaleString(),
                "Amount": txn.amount,
                "Currency": txn.currency,
                "Status": txn.status.toUpperCase(),
                "Card Type": txn.cardType,
                "Card Last 4": txn.cardLast4,
                "Acquirer": txn.acquirer,
                "Issuer": txn.issuer,
                "Timestamp": new Date(txn.timestamp).toLocaleString(),
            }));

            // Create workbook and worksheet
            const worksheet = XLSX.utils.json_to_sheet(exportData);
            const workbook = XLSX.utils.book_new();
            XLSX.utils.book_append_sheet(workbook, worksheet, "Transactions");

            // Auto-size columns
            const maxWidth = 20;
            const columnWidths = Object.keys(exportData[0] || {}).map((key) => ({
                wch: Math.min(
                    Math.max(
                        key.length,
                        ...exportData.map((row) => String(row[key as keyof typeof row]).length)
                    ),
                    maxWidth
                ),
            }));
            worksheet["!cols"] = columnWidths;

            // Generate filename with merchant ID and timestamp
            const timestamp = new Date().toISOString().split("T")[0];
            const filename = `transactions_${merchantId}_${timestamp}.xlsx`;

            // Download file
            XLSX.writeFile(workbook, filename);

            toast.success(`Successfully exported ${transactions.length} transactions`);
            setOpen(false);
        } catch (error) {
            console.error("Export failed:", error);
            toast.error("Failed to export transactions. Please try again.");
        } finally {
            setIsExporting(false);
        }
    };

    return (
        <AlertDialog open={open} onOpenChange={setOpen}>
            <AlertDialogTrigger asChild>
                <Button variant="default" className="gap-2">
                    <Download className="w-4 h-4" />
                    Export All
                </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
                <AlertDialogHeader>
                    <AlertDialogTitle>Export All Transactions</AlertDialogTitle>
                    <AlertDialogDescription>
                        This will export all transactions for merchant <strong>{merchantId}</strong> to an Excel file.
                        This may take a moment if there are many transactions.
                    </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel disabled={isExporting}>Cancel</AlertDialogCancel>
                    <AlertDialogAction
                        onClick={handleExport}
                        disabled={isExporting}
                        className="gap-2"
                    >
                        {isExporting ? (
                            <>
                                <Loader2 className="w-4 h-4 animate-spin" />
                                Exporting...
                            </>
                        ) : (
                            <>
                                <Download className="w-4 h-4" />
                                Export
                            </>
                        )}
                    </AlertDialogAction>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    );
};
