import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ITransactionLog } from '../transaction-log.model';
import { TransactionLogService } from '../service/transaction-log.service';

@Component({
  standalone: true,
  templateUrl: './transaction-log-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class TransactionLogDeleteDialogComponent {
  transactionLog?: ITransactionLog;

  constructor(
    protected transactionLogService: TransactionLogService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.transactionLogService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
