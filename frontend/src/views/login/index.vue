<template>
  <v-row
    align="center"
    justify="center"
  >
    <v-col
      cols="12"
      md="8"
    >
      <v-card
        ref="card"
        class="elevation-12"
        :loading="loading"
      >
        <v-toolbar
          color="#34558b"
          dark
          flat
        >
          <v-toolbar-title>{{ $t('login.title') }}</v-toolbar-title>
          <v-spacer />
        </v-toolbar>
        <v-card-text>
          <v-form
            id="login-form"
            ref="form"
            v-model="valid"
            @submit.prevent="valid && handleLogin()"
          >
            <v-text-field
              v-model="name"
              label="E-mail"
              prepend-icon="person"
              :rules="rules.email"
              autocomplete="email"
            />
            <v-text-field
              v-model="password"
              label="Lozinka"
              prepend-icon="lock"
              type="password"
              :rules="rules.password"
              autocomplete="current-password"
            />
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn
            name="register"
            to="/register"
          >
            {{ $t('login.register') }}
          </v-btn>
          <v-btn
            type="submit"
            name="submit"
            form="login-form"
            :disabled="!valid"
            color="primary"
          >
            {{ $t('login.submit') }}
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-col>
  </v-row>
</template>

<script lang="ts">
import { Component, Vue, Watch } from 'vue-property-decorator';
import { UserModule, AppModule } from '@/store';
import { Route } from 'vue-router';
import { isValidEmail } from '@/utils/validate';

@Component({
  name: 'Login',
})
export default class extends Vue {
  private name = '';

  private valid = true;

  private password = '';

  private redirect?: string;

  private loading = false;

  private rules = {
    email: [
      (v: string) => !!v || 'E-mail je obavezan',
      (v: string) => isValidEmail(v) || 'E-mail mora biti validan',
    ],
    password: [
      (v: string) => !!v || 'Lozinka je obavezna',
    ],
  };

  @Watch('$route', { immediate: true })
  private onRouteChange(route: Route) {
    const { query } = route;
    if (query.redirect) {
      this.redirect = query.redirect as string;
    }
  }

  private async handleLogin() {
    this.loading = true;

    const success = await UserModule.Login({
      name: this.name,
      password: this.password,
    });

    // this.$refs.form.reset();
    (this.$refs.form as Vue & { reset: () => void }).reset();

    if (success !== 200) {
      AppModule.setInfo({ message: `Logovanje nije uspelo: ${success}`, type: 'error' });
      this.loading = false;
      return;
    }

    if (!this.redirect) {
      if (UserModule.isDoctor) {
        this.redirect = '/my';
      }
      if (UserModule.isAdmin) {
        this.redirect = '/admin';
      }
    }

    await this.$router.push({
      path: this.redirect,
    });
  }

  logout() {
    UserModule.LogOut();
  }
}
</script>

<style lang="scss">
</style>
