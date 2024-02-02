import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BankInfoService } from '../service/bank-info.service';
import { IBankInfo } from '../bank-info.model';
import { BankInfoFormService } from './bank-info-form.service';

import { BankInfoUpdateComponent } from './bank-info-update.component';

describe('BankInfo Management Update Component', () => {
  let comp: BankInfoUpdateComponent;
  let fixture: ComponentFixture<BankInfoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bankInfoFormService: BankInfoFormService;
  let bankInfoService: BankInfoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), BankInfoUpdateComponent],
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
      .overrideTemplate(BankInfoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BankInfoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bankInfoFormService = TestBed.inject(BankInfoFormService);
    bankInfoService = TestBed.inject(BankInfoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const bankInfo: IBankInfo = { id: 456 };

      activatedRoute.data = of({ bankInfo });
      comp.ngOnInit();

      expect(comp.bankInfo).toEqual(bankInfo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBankInfo>>();
      const bankInfo = { id: 123 };
      jest.spyOn(bankInfoFormService, 'getBankInfo').mockReturnValue(bankInfo);
      jest.spyOn(bankInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bankInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bankInfo }));
      saveSubject.complete();

      // THEN
      expect(bankInfoFormService.getBankInfo).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bankInfoService.update).toHaveBeenCalledWith(expect.objectContaining(bankInfo));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBankInfo>>();
      const bankInfo = { id: 123 };
      jest.spyOn(bankInfoFormService, 'getBankInfo').mockReturnValue({ id: null });
      jest.spyOn(bankInfoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bankInfo: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bankInfo }));
      saveSubject.complete();

      // THEN
      expect(bankInfoFormService.getBankInfo).toHaveBeenCalled();
      expect(bankInfoService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBankInfo>>();
      const bankInfo = { id: 123 };
      jest.spyOn(bankInfoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bankInfo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bankInfoService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
