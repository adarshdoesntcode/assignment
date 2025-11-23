import { Spinner } from "@/components/ui/spinner";
import { useMerchantDetails } from "@/hooks/useMerchantDetails";
import { Transactions } from "@/pages/Transaction/Transactions";
import { useParams } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import {
  Building2,
  Mail,
  Phone,
  FileText,
  Hash,
  Calendar,
  Activity,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { EditMerchant } from "./EditMerchant";
import { useMerchantMutation } from "@/hooks/useMerchantMutation";
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

function MerchantDetails() {
  const { id } = useParams();
  if (!id) {
    return <div>Invalid merchant ID</div>;
  }
  const { data, loading, error, refetch } = useMerchantDetails(id);
  const { mutate: toggleStatus, loading: toggling } = useMerchantMutation();

  const handleToggleStatus = async () => {
    if (!id || !data) return;
    await toggleStatus(id, { isActive: !data.isActive });
    refetch();
  };

  const formatDate = (dateString: string | undefined) => {
    if (!dateString) return "N/A";
    try {
      const date = new Date(dateString);
      return date.toLocaleString("en-US", {
        month: "short",
        day: "numeric",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch (error) {
      return dateString;
    }
  };

  return (
    <main className="container py-6 mx-auto">
      <div className="px-4 sm:px-8">
        <h1 className="text-2xl font-bold">Merchant Details</h1>
        <p className="text-muted-foreground">
          View and manage merchant information.
        </p>
      </div>
      <div>
        {loading ? (
          <div className="flex justify-center items-center w-full h-[300px]">
            <Spinner />
          </div>
        ) : error ? (
          <div className="px-4 text-red-500 sm:px-8">
            Error: {error.message}
          </div>
        ) : (
          <Card className="mx-4 mt-6 sm:mx-8">
            <CardHeader>
              <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div className="min-w-0 space-y-1">
                  <CardTitle className="flex flex-wrap items-center gap-2 text-xl">
                    {data?.merchantName || "N/A"}
                    <Badge
                      variant={data?.isActive ? "default" : "secondary"}
                      className="shrink-0"
                    >
                      {data?.isActive ? "Active" : "Inactive"}
                    </Badge>
                  </CardTitle>
                  <p className="text-sm truncate text-muted-foreground">
                    {data?.businessName || "No business name"}
                  </p>
                </div>
                <div className="flex gap-3 sm:shrink-0">
                  {data?.isActive && (
                    <EditMerchant
                      merchantData={data || undefined}
                      onSuccess={refetch}
                    />
                  )}
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button
                        variant={data?.isActive ? "destructive" : "default"}
                        disabled={toggling}
                        className="w-auto"
                      >
                        {toggling
                          ? "Processing..."
                          : data?.isActive
                            ? "Deactivate"
                            : "Activate"}
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>
                          {data?.isActive
                            ? "Deactivate Merchant?"
                            : "Activate Merchant?"}
                        </AlertDialogTitle>
                        <AlertDialogDescription>
                          {data?.isActive
                            ? `Are you sure you want to deactivate ${data?.merchantName}? This will prevent them from processing new transactions.`
                            : `Are you sure you want to activate ${data?.merchantName}? This will allow them to process transactions again.`}
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Cancel</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={handleToggleStatus}
                          className={
                            data?.isActive
                              ? "bg-destructive text-destructive-foreground hover:bg-destructive/90"
                              : ""
                          }
                        >
                          {data?.isActive ? "Deactivate" : "Activate"}
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </div>
              </div>
            </CardHeader>

            <CardContent className="space-y-6">
              <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                <InfoRow
                  icon={Hash}
                  label="Merchant ID"
                  value={data?.merchantId}
                />
                <InfoRow
                  icon={Building2}
                  label="Business Type"
                  value={data?.businessType?.toUpperCase()}
                />
                <InfoRow icon={Mail} label="Email" value={data?.email} />
                <InfoRow icon={Phone} label="Phone" value={data?.phone} />
                <InfoRow icon={FileText} label="Tax ID" value={data?.taxId} />
                <InfoRow
                  icon={FileText}
                  label="Registration No"
                  value={data?.registrationNumber}
                />
              </div>

              <div className="pt-4 border-t">
                <div className="flex items-center gap-2 mb-3">
                  <Activity className="w-4 h-4 text-muted-foreground" />
                  <span className="text-sm font-semibold tracking-wide uppercase text-muted-foreground">
                    Activity Log
                  </span>
                </div>
                <div className="grid gap-4 sm:grid-cols-2">
                  <div className="flex items-center gap-2 text-sm">
                    <Calendar className="w-4 h-4 text-muted-foreground" />
                    <span className="text-muted-foreground">Created:</span>
                    <span className="font-medium">
                      {formatDate(data?.createdAt)}
                    </span>
                  </div>
                  <div className="flex items-center gap-2 text-sm">
                    <Calendar className="w-4 h-4 text-muted-foreground" />
                    <span className="text-muted-foreground">Last Updated:</span>
                    <span className="font-medium">
                      {formatDate(data?.updatedAt)}
                    </span>
                  </div>
                </div>
              </div>
            </CardContent>
          </Card>
        )}
      </div>

      {data?.isActive && (
        <Transactions
          showTitle={false}
          showPagination={false}
          showFilters={false}
          merchantId={id}
          showExport={true}
          defaultFilters={{ page: 0, size: 5 }}
        />
      )}
    </main>
  );
}

const InfoRow = ({
  icon: Icon,
  label,
  value,
}: {
  icon: React.ComponentType<{ className?: string }>;
  label: string;
  value?: string;
}) => (
  <div className="flex items-center gap-2 text-sm">
    <Icon className="h-3.5 w-3.5 text-muted-foreground shrink-0" />
    <span className="text-muted-foreground min-w-[120px]">{label}:</span>
    <span className="font-medium truncate">{value || "N/A"}</span>
  </div>
);

export default MerchantDetails;
