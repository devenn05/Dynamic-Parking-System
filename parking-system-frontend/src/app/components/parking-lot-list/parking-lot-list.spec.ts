import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotList } from './parking-lot-list';

describe('ParkingLotList', () => {
  let component: ParkingLotList;
  let fixture: ComponentFixture<ParkingLotList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParkingLotList]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParkingLotList);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
