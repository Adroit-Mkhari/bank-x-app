import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TransactionLogService } from '../service/transaction-log.service';
import { ITransactionLog } from '../transaction-log.model';
import { TransactionLogFormService } from './transaction-log-form.service';

import { TransactionLogUpdateComponent } from './transaction-log-update.component';

describe('TransactionLog Management Update Component', () => {
  let comp: TransactionLogUpdateComponent;
  let fixture: ComponentFixture<TransactionLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transactionLogFormService: TransactionLogFormService;
  let transactionLogService: TransactionLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), TransactionLogUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(TransactionLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransactionLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transactionLogFormService = TestBed.inject(TransactionLogFormService);
    transactionLogService = TestBed.inject(TransactionLogService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const transactionLog: ITransactionLog = { uniqueTransactionId: '1361f429-3817-4123-8ee3-fdf8943310b2' };

      activatedRoute.data = of({ transactionLog });
      comp.ngOnInit();

      expect(comp.transactionLog).toEqual(transactionLog);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransactionLog>>();
      const transactionLog = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(transactionLogFormService, 'getTransactionLog').mockReturnValue(transactionLog);
      jest.spyOn(transactionLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transactionLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transactionLog }));
      saveSubject.complete();

      // THEN
      expect(transactionLogFormService.getTransactionLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(transactionLogService.update).toHaveBeenCalledWith(expect.objectContaining(transactionLog));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransactionLog>>();
      const transactionLog = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(transactionLogFormService, 'getTransactionLog').mockReturnValue({ uniqueTransactionId: null });
      jest.spyOn(transactionLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transactionLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transactionLog }));
      saveSubject.complete();

      // THEN
      expect(transactionLogFormService.getTransactionLog).toHaveBeenCalled();
      expect(transactionLogService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ITransactionLog>>();
      const transactionLog = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
      jest.spyOn(transactionLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transactionLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transactionLogService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
