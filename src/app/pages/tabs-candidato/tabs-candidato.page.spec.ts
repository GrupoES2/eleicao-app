import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TabsCandidatoPage } from './tabs-candidato.page';

describe('TabsCandidatoPage', () => {
  let component: TabsCandidatoPage;
  let fixture: ComponentFixture<TabsCandidatoPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(TabsCandidatoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
