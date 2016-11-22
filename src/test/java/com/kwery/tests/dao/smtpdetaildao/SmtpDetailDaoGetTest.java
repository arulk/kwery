package com.kwery.tests.dao.smtpdetaildao;

import com.kwery.dao.SmtpDetailDao;
import com.kwery.models.SmtpDetail;
import com.kwery.tests.fluentlenium.utils.DbUtil;
import com.kwery.tests.util.RepoDashDaoTestBase;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.exparity.hamcrest.BeanMatchers.theSameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

public class SmtpDetailDaoGetTest extends RepoDashDaoTestBase {
    protected SmtpDetailDao smtpDetailDao;

    protected Map<Integer, SmtpDetail> idDetailMap = new HashMap<>();

    @Before
    public void setUpSmtpDetailDaoGetTest() {
        for (int i = 1; i < 3; ++i) {
            SmtpDetail smtpDetail = new SmtpDetail();
            smtpDetail.setId(i);
            smtpDetail.setHost("foo.com");
            smtpDetail.setPort(465);
            smtpDetail.setSsl(true);
            smtpDetail.setUsername("username");
            smtpDetail.setPassword("password");

            idDetailMap.put(i, smtpDetail);

            DbSetup dbSetup = new DbSetup(new DataSourceDestination(DbUtil.getDatasource()),
                    Operations.sequenceOf(
                            insertInto(SmtpDetail.TABLE_SMTP_DETAILS)
                                    .row()
                                    .column(SmtpDetail.COLUMN_ID, smtpDetail.getId())
                                    .column(SmtpDetail.COLUMN_HOST, smtpDetail.getHost())
                                    .column(SmtpDetail.COLUMN_PORT, smtpDetail.getPort())
                                    .column(SmtpDetail.COLUMN_SSL, smtpDetail.isSsl())
                                    .column(SmtpDetail.COLUMN_USERNAME, smtpDetail.getUsername())
                                    .column(SmtpDetail.COLUMN_PASSWORD, smtpDetail.getPassword())
                                    .end()
                                    .build()
                    )
            );

            dbSetup.launch();
        }

        smtpDetailDao = getInstance(SmtpDetailDao.class);
    }

    @Test
    public void testGet() {
        List<SmtpDetail> smtpDetailList = smtpDetailDao.get();
        assertThat(smtpDetailList, hasSize(2));
        assertThat(smtpDetailList.get(0) , theSameBeanAs(idDetailMap.get(smtpDetailList.get(0).getId())));
        assertThat(smtpDetailList.get(1) , theSameBeanAs(idDetailMap.get(smtpDetailList.get(1).getId())));
    }

    @Test
    public void testGetById() {
        assertThat(smtpDetailDao.get(1), theSameBeanAs(idDetailMap.get(1)));
        assertThat(smtpDetailDao.get(2), theSameBeanAs(idDetailMap.get(2)));
    }
}
