import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CheckCancelComponent } from './check-cancel.component';

describe('CheckCancelComponent', () => {
  let component: CheckCancelComponent;
  let fixture: ComponentFixture<CheckCancelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CheckCancelComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CheckCancelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
