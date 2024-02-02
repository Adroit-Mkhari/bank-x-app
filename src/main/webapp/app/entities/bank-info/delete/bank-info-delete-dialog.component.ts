import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IBankInfo } from '../bank-info.model';
import { BankInfoService } from '../service/bank-info.service';

@Component({
  standalone: true,
  templateUrl: './bank-info-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class BankInfoDeleteDialogComponent {
  bankInfo?: IBankInfo;

  constructor(
    protected bankInfoService: BankInfoService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bankInfoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
