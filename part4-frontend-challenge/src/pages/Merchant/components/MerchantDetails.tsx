import { Transactions } from "@/pages/Transaction/Transactions";
import { useParams } from "react-router-dom";

function MerchantDetails() {
  const { id } = useParams();

  return (
    <main className="container">
      <div>
        <h1 className="text-2xl font-bold">Merchant Details</h1>
        <p>This is the merchant details page.</p>
      </div>
      <Transactions
        showTitle={false}
        showPagination={false}
        showFilters={false}
        merchantId={id}
        defaultFilters={{ page: 0, size: 5 }}
      />
    </main>
  );
}

export default MerchantDetails;
