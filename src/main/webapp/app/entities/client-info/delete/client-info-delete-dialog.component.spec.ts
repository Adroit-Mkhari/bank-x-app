jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ClientInfoService } from '../service/client-info.service';

import { ClientInfoDeleteDialogComponent } from './client-info-delete-dialog.component';

describe('ClientInfo Management Delete Component', () => {
  let comp: ClientInfoDeleteDialogComponent;
  let fixture: ComponentFixture<ClientInfoDeleteDialogComponent>;
  let service: ClientInfoService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, ClientInfoDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(ClientInfoDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ClientInfoDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ClientInfoService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete('ABC');
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith('ABC');
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      }),
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
