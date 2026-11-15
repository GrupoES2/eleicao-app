import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GerenciarEleicaoPage } from './gerenciar-eleicao.page';

describe('GerenciarEleicaoPage', () => {
  let component: GerenciarEleicaoPage;
  let fixture: ComponentFixture<GerenciarEleicaoPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(GerenciarEleicaoPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
