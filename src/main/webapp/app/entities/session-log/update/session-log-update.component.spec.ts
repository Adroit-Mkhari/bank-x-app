import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ITransactionLog } from 'app/entities/transaction-log/transaction-log.model';
import { TransactionLogService } from 'app/entities/transaction-log/service/transaction-log.service';
import { SessionLogService } from '../service/session-log.service';
import { ISessionLog } from '../session-log.model';
import { SessionLogFormService } from './session-log-form.service';

import { SessionLogUpdateComponent } from './session-log-update.component';

describe('SessionLog Management Update Component', () => {
  let comp: SessionLogUpdateComponent;
  let fixture: ComponentFixture<SessionLogUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let sessionLogFormService: SessionLogFormService;
  let sessionLogService: SessionLogService;
  let transactionLogService: TransactionLogService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), SessionLogUpdateComponent],
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
      .overrideTemplate(SessionLogUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SessionLogUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    sessionLogFormService = TestBed.inject(SessionLogFormService);
    sessionLogService = TestBed.inject(SessionLogService);
    transactionLogService = TestBed.inject(TransactionLogService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call TransactionLog query and add missing value', () => {
      const sessionLog: ISessionLog = { id: 456 };
      const transactionLog: ITransactionLog = { uniqueTransactionId: 'af8a0a97-0eb1-479a-b1e4-e2c771e285a1' };
      sessionLog.transactionLog = transactionLog;

      const transactionLogCollection: ITransactionLog[] = [{ uniqueTransactionId: '270ab7e7-c517-4607-a168-a1b7e51252e9' }];
      jest.spyOn(transactionLogService, 'query').mockReturnValue(of(new HttpResponse({ body: transactionLogCollection })));
      const additionalTransactionLogs = [transactionLog];
      const expectedCollection: ITransactionLog[] = [...additionalTransactionLogs, ...transactionLogCollection];
      jest.spyOn(transactionLogService, 'addTransactionLogToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ sessionLog });
      comp.ngOnInit();

      expect(transactionLogService.query).toHaveBeenCalled();
      expect(transactionLogService.addTransactionLogToCollectionIfMissing).toHaveBeenCalledWith(
        transactionLogCollection,
        ...additionalTransactionLogs.map(expect.objectContaining),
      );
      expect(comp.transactionLogsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const sessionLog: ISessionLog = { id: 456 };
      const transactionLog: ITransactionLog = { uniqueTransactionId: 'b233b52c-18c2-4057-a7e4-a3a281f49614' };
      sessionLog.transactionLog = transactionLog;

      activatedRoute.data = of({ sessionLog });
      comp.ngOnInit();

      expect(comp.transactionLogsSharedCollection).toContain(transactionLog);
      expect(comp.sessionLog).toEqual(sessionLog);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionLog>>();
      const sessionLog = { id: 123 };
      jest.spyOn(sessionLogFormService, 'getSessionLog').mockReturnValue(sessionLog);
      jest.spyOn(sessionLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionLog }));
      saveSubject.complete();

      // THEN
      expect(sessionLogFormService.getSessionLog).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(sessionLogService.update).toHaveBeenCalledWith(expect.objectContaining(sessionLog));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionLog>>();
      const sessionLog = { id: 123 };
      jest.spyOn(sessionLogFormService, 'getSessionLog').mockReturnValue({ id: null });
      jest.spyOn(sessionLogService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionLog: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: sessionLog }));
      saveSubject.complete();

      // THEN
      expect(sessionLogFormService.getSessionLog).toHaveBeenCalled();
      expect(sessionLogService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ISessionLog>>();
      const sessionLog = { id: 123 };
      jest.spyOn(sessionLogService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ sessionLog });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(sessionLogService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTransactionLog', () => {
      it('Should forward to transactionLogService', () => {
        const entity = { uniqueTransactionId: '9fec3727-3421-4967-b213-ba36557ca194' };
        const entity2 = { uniqueTransactionId: '1361f429-3817-4123-8ee3-fdf8943310b2' };
        jest.spyOn(transactionLogService, 'compareTransactionLog');
        comp.compareTransactionLog(entity, entity2);
        expect(transactionLogService.compareTransactionLog).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
