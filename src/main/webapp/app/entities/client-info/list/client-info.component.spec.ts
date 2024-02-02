import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ClientInfoService } from '../service/client-info.service';

import { ClientInfoComponent } from './client-info.component';
import SpyInstance = jest.SpyInstance;

describe('ClientInfo Management Component', () => {
  let comp: ClientInfoComponent;
  let fixture: ComponentFixture<ClientInfoComponent>;
  let service: ClientInfoService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'client-info', component: ClientInfoComponent }]),
        HttpClientTestingModule,
        ClientInfoComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'idNumber,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'idNumber,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ClientInfoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientInfoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ClientInfoService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ idNumber: 'ABC' }],
          headers,
        }),
      ),
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.clientInfos?.[0]).toEqual(expect.objectContaining({ idNumber: 'ABC' }));
  });

  describe('trackIdNumber', () => {
    it('Should forward to clientInfoService', () => {
      const entity = { idNumber: 'ABC' };
      jest.spyOn(service, 'getClientInfoIdentifier');
      const idNumber = comp.trackIdNumber(0, entity);
      expect(service.getClientInfoIdentifier).toHaveBeenCalledWith(entity);
      expect(idNumber).toBe(entity.idNumber);
    });
  });

  it('should load a page', () => {
    // WHEN
    comp.navigateToPage(1);

    // THEN
    expect(routerNavigateSpy).toHaveBeenCalled();
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['idNumber,desc'] }));
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // GIVEN
    comp.predicate = 'name';

    // WHEN
    comp.navigateToWithComponentValues();

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['name,asc'],
        }),
      }),
    );
  });
});
