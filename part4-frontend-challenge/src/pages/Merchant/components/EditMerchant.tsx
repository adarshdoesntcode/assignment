import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogClose,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Edit, Loader2 } from "lucide-react";
import { useMerchantMutation } from "@/hooks/useMerchantMutation";
import { MerchantDetail } from "@/types/merchant";

interface EditMerchantProps {
  merchantData?: MerchantDetail;
  onSuccess?: () => void;
}

interface MerchantFormData {
  email: string;
  phone: string;
}

export function EditMerchant({ merchantData, onSuccess }: EditMerchantProps) {
  const [open, setOpen] = useState(false);
  const { mutate, loading, success } = useMerchantMutation();
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<MerchantFormData>();

  // Reset form when dialog opens with merchant data
  useEffect(() => {
    if (open && merchantData) {
      reset({
        email: merchantData.email,
        phone: merchantData.phone,
      });
    }
  }, [open, merchantData, reset]);

  // Close dialog on success
  useEffect(() => {
    if (success) {
      setOpen(false);
      if (onSuccess) {
        onSuccess();
      }
    }
  }, [success, onSuccess]);

  const onSubmit = async (data: MerchantFormData) => {
    if (!merchantData?.merchantId) return;

    await mutate(merchantData.merchantId, {
      email: data.email,
      phone: data.phone,
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline" className="shrink-0">
          Edit <Edit className="w-4 h-4 ml-2" />
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[525px]">
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogHeader>
            <DialogTitle>Edit Merchant Information</DialogTitle>
            <DialogDescription>
              Update merchant contact information. Click save when you're done.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="email">Email Address</Label>
              <Input
                id="email"
                type="email"
                placeholder="merchant@example.com"
                {...register("email", {
                  required: "Email is required",
                  pattern: {
                    value: /^[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,}$/i,
                    message: "Invalid email address",
                  },
                })}
              />
              {errors.email && (
                <p className="text-sm text-red-600">{errors.email.message}</p>
              )}
            </div>
            <div className="grid gap-2">
              <Label htmlFor="phone">Phone Number</Label>
              <Input
                id="phone"
                type="tel"
                placeholder="9XXXXXXXXX"
                {...register("phone", {
                  required: "Phone number is required",
                  pattern: {
                    value: /^(\+977[-\s]?)?[9][0-9]{9}$/,
                    message: "Invalid Nepali phone number",
                  },
                })}
              />
              {errors.phone && (
                <p className="text-sm text-red-600">{errors.phone.message}</p>
              )}
            </div>
          </div>
          <DialogFooter>
            <DialogClose asChild>
              <Button variant="outline" type="button" disabled={loading}>
                Cancel
              </Button>
            </DialogClose>
            <Button type="submit" disabled={loading}>
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Saving...
                </>
              ) : (
                "Save changes"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
