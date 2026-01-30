import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingSessions } from './parking-sessions';

describe('ParkingSessions', () => {
  let component: ParkingSessions;
  let fixture: ComponentFixture<ParkingSessions>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ParkingSessions]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ParkingSessions);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
