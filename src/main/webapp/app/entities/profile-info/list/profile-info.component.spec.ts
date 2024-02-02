import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ProfileInfoService } from '../service/profile-info.service';

import { ProfileInfoComponent } from './profile-info.component';
import SpyInstance = jest.SpyInstance;

describe('ProfileInfo Management Component', () => {
  let comp: ProfileInfoComponent;
  let fixture: ComponentFixture<ProfileInfoComponent>;
  let service: ProfileInfoService;
  let routerNavigateSpy: SpyInstance<Promise<boolean>>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([{ path: 'profile-info', component: ProfileInfoComponent }]),
        HttpClientTestingModule,
        ProfileInfoComponent,
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'profileNumber,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'profileNumber,desc',
              }),
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ProfileInfoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProfileInfoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ProfileInfoService);
    routerNavigateSpy = jest.spyOn(comp.router, 'navigate');

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ profileNumber: 'ABC' }],
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
    expect(comp.profileInfos?.[0]).toEqual(expect.objectContaining({ profileNumber: 'ABC' }));
  });

  describe('trackProfileNumber', () => {
    it('Should forward to profileInfoService', () => {
      const entity = { profileNumber: 'ABC' };
      jest.spyOn(service, 'getProfileInfoIdentifier');
      const profileNumber = comp.trackProfileNumber(0, entity);
      expect(service.getProfileInfoIdentifier).toHaveBeenCalledWith(entity);
      expect(profileNumber).toBe(entity.profileNumber);
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
    expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['profileNumber,desc'] }));
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

  it('should re-initialize the page', () => {
    // WHEN
    comp.loadPage(1);
    comp.reset();

    // THEN
    expect(comp.page).toEqual(1);
    expect(service.query).toHaveBeenCalledTimes(2);
    expect(comp.profileInfos?.[0]).toEqual(expect.objectContaining({ profileNumber: 'ABC' }));
  });
});
