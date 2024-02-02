import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IClientInfo } from '../client-info.model';
import { ClientInfoService } from '../service/client-info.service';

@Component({
  standalone: true,
  templateUrl: './client-info-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ClientInfoDeleteDialogComponent {
  clientInfo?: IClientInfo;

  constructor(
    protected clientInfoService: ClientInfoService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.clientInfoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
