export interface IBankInfo {
  id: number;
  swiftCode?: string | null;
  bankName?: string | null;
}

export type NewBankInfo = Omit<IBankInfo, 'id'> & { id: null };
