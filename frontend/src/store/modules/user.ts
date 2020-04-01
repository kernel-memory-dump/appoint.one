import {
  VuexModule, Module, Action, Mutation, getModule,
} from 'vuex-module-decorators';
import UserApi from '@/api/UserApi';
import AppCookies from '@/utils/cookies';
import { resetRouter } from '@/router';
import store from '@/store';
import { User } from '@/model/User';

export interface UserState {
  id: number,
  token: string
  name: string
  roles: string[]
}

@Module({ dynamic: true, store, name: 'user' })
class UserModuleClass extends VuexModule implements UserState {
  public token = AppCookies.getToken() || '';

  public id = 0;

  public name = '';

  public roles: string[] = [];

  @Mutation
  private SET_TOKEN(token: string) {
    this.token = token;
  }

  @Mutation
  private SET_NAME(name: string) {
    this.name = name;
  }

  @Mutation
  private SET_ID(id: number) {
    this.id = id;
  }

  @Mutation
  private SET_ROLES(roles: string[]) {
    this.roles = roles;
  }

  @Action
  public async Login(userInfo: { name: string, password: string}): Promise<boolean> {
    let { name } = userInfo;
    name = name.trim();
    const { password } = userInfo;
    const res = await UserApi.login({ name, password }).catch((_) => ({ data: { token: '' } }));
    const data: User = res && res.data;

    if (data.token === '') {
      return false;
    }

    AppCookies.setToken(data.token);
    this.SET_TOKEN(data.token);
    this.SET_ROLES([data.role]);
    this.SET_NAME(data.name);
    this.SET_ID(data.id);
    return true;
  }

  @Action
  public ResetToken() {
    AppCookies.removeToken();
    this.SET_TOKEN('');
    this.SET_ID(0);
    this.SET_NAME('');
    this.SET_ROLES([]);
  }

  @Action
  public async GetUserInfo() {
    if (this.token === '') {
      throw Error('GetUserInfo: token is undefined!');
    }
    const { data } = await UserApi.getUserInfo({ });
    if (!data) {
      throw Error('Verification failed, please Login again.');
    }
    const {
      roles, name,
    } = data.user;
    // roles must be a non-empty array
    if (!roles || roles.length <= 0) {
      throw Error('GetUserInfo: roles must be a non-null array!');
    }
    this.SET_ROLES(roles);
    this.SET_NAME(name);
  }

  @Action
  public async LogOut() {
    await UserApi.logout();
    this.ResetToken();
    resetRouter();
  }

  /**
   * Returns true if user is logged in.
   */
  get isUserLoggedIn() {
    return this.token !== '';
  }

  get hasAccess(): (permissionRoles: string[]) => boolean {
    return (permissionRoles: string[]) => this.roles.some((role) => permissionRoles.includes(role));
  }
}

export const UserModule = getModule(UserModuleClass);
