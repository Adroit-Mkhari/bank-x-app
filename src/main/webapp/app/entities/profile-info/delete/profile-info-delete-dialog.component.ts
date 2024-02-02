import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IProfileInfo } from '../profile-info.model';
import { ProfileInfoService } from '../service/profile-info.service';

@Component({
  standalone: true,
  templateUrl: './profile-info-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class ProfileInfoDeleteDialogComponent {
  profileInfo?: IProfileInfo;

  constructor(
    protected profileInfoService: ProfileInfoService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.profileInfoService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
