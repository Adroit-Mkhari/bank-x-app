import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IClientInbox } from '../client-inbox.model';
import { ClientInboxService } from '../service/client-inbox.service';

@Component({
  standalone: true,
  templateUrl: './client-inbox-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ClientInboxDeleteDialogComponent {
  clientInbox?: IClientInbox;

  constructor(
    protected clientInboxService: ClientInboxService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.clientInboxService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
