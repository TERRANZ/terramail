package com.terramail.repository;

import com.terramail.model.AccountSettings;

public interface AccountSettingsRepository {

    AccountSettings findById(long id);

    AccountSettings save(AccountSettings settings);

    void deleteById(long id);

    AccountSettings findByAccountName(String accountName);
}
