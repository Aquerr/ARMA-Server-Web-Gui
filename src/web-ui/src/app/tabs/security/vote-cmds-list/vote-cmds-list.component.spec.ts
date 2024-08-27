import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VoteCmdsListComponent } from './vote-cmds-list.component';

describe('VoteCmdsListComponent', () => {
  let component: VoteCmdsListComponent;
  let fixture: ComponentFixture<VoteCmdsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoteCmdsListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(VoteCmdsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
