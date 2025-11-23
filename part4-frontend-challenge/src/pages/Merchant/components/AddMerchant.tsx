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
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Loader2, PlusCircle } from "lucide-react";
import { createMerchant } from "@/services/merchantService";
import { MerchantCreatePayload } from "@/types/merchant";
import { toast } from "sonner";

interface AddMerchantProps {
  onSuccess?: () => void;
}

interface MerchantFormData {
  merchantName: string;
  businessName: string;
  email: string;
  phone: string;
  businessType: string;
  taxId: string;
  registrationNumber: string;
}

export function AddMerchant({ onSuccess }: AddMerchantProps) {
  const [open, setOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const {
    register,
    handleSubmit,
    reset,
    setValue,
    watch,
    formState: { errors },
  } = useForm<MerchantFormData>();

  const businessType = watch("businessType");

  useEffect(() => {
    if (!open) {
      reset();
    }
  }, [open, reset]);

  const onSubmit = async (data: MerchantFormData) => {
    try {
      setLoading(true);
      const payload: MerchantCreatePayload = {
        merchantName: data.merchantName,
        businessName: data.businessName,
        email: data.email,
        phone: data.phone,
        businessType: data.businessType,
        taxId: data.taxId,
        registrationNumber: data.registrationNumber,
      };

      const response = await createMerchant(payload);

      if (response.success) {
        toast.success(response.message || "Merchant created successfully");
        setOpen(false);
        reset();
        if (onSuccess) {
          onSuccess();
        }
      }
    } catch (error: any) {
      console.error("Error creating merchant:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="lg" variant="default" className="w-full md:w-auto">
          Add Merchant
          <PlusCircle className="w-4 h-4" />
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[625px] max-h-[90vh] overflow-y-auto">
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogHeader>
            <DialogTitle>Add New Merchant</DialogTitle>
            <DialogDescription>
              Enter the merchant details below. All fields are required.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="merchantName">
                Merchant Name <span className="text-red-500">*</span>
              </Label>
              <Input
                id="merchantName"
                type="text"
                placeholder="TechHub Electronics"
                {...register("merchantName", {
                  required: "Merchant name is required",
                  minLength: {
                    value: 3,
                    message: "Merchant name must be at least 3 characters",
                  },
                  maxLength: {
                    value: 100,
                    message: "Merchant name must not exceed 100 characters",
                  },
                })}
              />
              {errors.merchantName && (
                <p className="text-sm text-red-600">
                  {errors.merchantName.message}
                </p>
              )}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="businessName">
                Business Name <span className="text-red-500">*</span>
              </Label>
              <Input
                id="businessName"
                type="text"
                placeholder="TechHub Electronics LLC"
                {...register("businessName", {
                  required: "Business name is required",
                  minLength: {
                    value: 3,
                    message: "Business name must be at least 3 characters",
                  },
                  maxLength: {
                    value: 100,
                    message: "Business name must not exceed 100 characters",
                  },
                })}
              />
              {errors.businessName && (
                <p className="text-sm text-red-600">
                  {errors.businessName.message}
                </p>
              )}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="email">
                Email Address <span className="text-red-500">*</span>
              </Label>
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
              <Label htmlFor="phone">
                Phone Number <span className="text-red-500">*</span>
              </Label>
              <Input
                id="phone"
                type="tel"
                placeholder="9818576955"
                {...register("phone", {
                  required: "Phone number is required",
                  pattern: {
                    value: /^(\+977[-\s]?)?[9][0-9]{9}$/,
                    message:
                      "Invalid phone number (should start with 9 and be 10 digits)",
                  },
                })}
              />
              {errors.phone && (
                <p className="text-sm text-red-600">{errors.phone.message}</p>
              )}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="businessType">
                Business Type <span className="text-red-500">*</span>
              </Label>
              <Select
                value={businessType}
                onValueChange={(value) => setValue("businessType", value)}
              >
                <SelectTrigger
                  className={
                    errors.businessType ? "border-red-500 w-full" : "w-full"
                  }
                >
                  <SelectValue placeholder="Select business type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="retail">Retail</SelectItem>
                  <SelectItem value="wholesale">Wholesale</SelectItem>
                  <SelectItem value="services">Services</SelectItem>
                  <SelectItem value="manufacturing">Manufacturing</SelectItem>
                  <SelectItem value="ecommerce">E-commerce</SelectItem>
                  <SelectItem value="hospitality">Hospitality</SelectItem>
                  <SelectItem value="other">Other</SelectItem>
                </SelectContent>
              </Select>
              <input
                type="hidden"
                {...register("businessType", {
                  required: "Business type is required",
                })}
              />
              {errors.businessType && (
                <p className="text-sm text-red-600">
                  {errors.businessType.message}
                </p>
              )}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="taxId">
                Tax ID <span className="text-red-500">*</span>
              </Label>
              <Input
                id="taxId"
                type="text"
                placeholder="123456789"
                {...register("taxId", {
                  required: "Tax ID is required",
                  pattern: {
                    value: /^[0-9]{9,15}$/,
                    message: "Tax ID must be 9-15 digits",
                  },
                })}
              />
              {errors.taxId && (
                <p className="text-sm text-red-600">{errors.taxId.message}</p>
              )}
            </div>

            <div className="grid gap-2">
              <Label htmlFor="registrationNumber">
                Registration Number <span className="text-red-500">*</span>
              </Label>
              <Input
                id="registrationNumber"
                type="text"
                placeholder="REG-2024-001"
                {...register("registrationNumber", {
                  required: "Registration number is required",
                  minLength: {
                    value: 3,
                    message:
                      "Registration number must be at least 3 characters",
                  },
                  maxLength: {
                    value: 50,
                    message:
                      "Registration number must not exceed 50 characters",
                  },
                })}
              />
              {errors.registrationNumber && (
                <p className="text-sm text-red-600">
                  {errors.registrationNumber.message}
                </p>
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
                  Creating...
                </>
              ) : (
                "Create Merchant"
              )}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
