import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IAccountInfo } from '../account-info.model';
import { AccountInfoService } from '../service/account-info.service';

@Component({
  standalone: true,
  templateUrl: './account-info-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class AccountInfoDeleteDialogComponent {
  accountInfo?: IAccountInfo;

  constructor(
    protected accountInfoService: AccountInfoService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.accountInfoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
