export class Registration {
  constructor(
    public login: string,
    public email: string,
    public idNumber: string,
    public firstName: string,
    public lastName: string,
    public streetAddress: string,
    public postalCode: string,
    public city: string,
    public stateProvince: string,
    public phoneNumber: string,
    public password: string,
    public langKey: string,
  ) {}
}
