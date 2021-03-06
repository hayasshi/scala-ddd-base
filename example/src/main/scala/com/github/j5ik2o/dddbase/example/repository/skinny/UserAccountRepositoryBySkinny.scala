package com.github.j5ik2o.dddbase.example.repository.skinny

import cats.data.ReaderT
import com.github.j5ik2o.dddbase.example.dao.skinny.UserAccountComponent
import com.github.j5ik2o.dddbase.example.model._
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository
import com.github.j5ik2o.dddbase.example.repository.UserAccountRepository.BySkinny
import com.github.j5ik2o.dddbase.skinny.AggregateIOBaseFeature.RIO
import com.github.j5ik2o.dddbase.skinny._
import monix.eval.Task

class UserAccountRepositoryBySkinny
    extends UserAccountRepository[BySkinny]
    with AggregateSingleReadFeature
    with AggregateSingleWriteFeature
    with AggregateMultiReadFeature
    with AggregateMultiWriteFeature
    with AggregateSingleSoftDeleteFeature
    with AggregateMultiSoftDeleteFeature
    with UserAccountComponent {

  override type RecordType = UserAccountRecord
  override type DaoType    = UserAccountDao.type
  override protected val dao: UserAccountDao.type = UserAccountDao

  override protected def convertToAggregate: UserAccountRecord => RIO[UserAccount] = { record =>
    ReaderT { _ =>
      Task.pure {
        UserAccount(
          id = UserAccountId(record.id),
          status = Status.withName(record.status),
          emailAddress = EmailAddress(record.email),
          password = HashedPassword(record.password),
          firstName = record.firstName,
          lastName = record.lastName,
          createdAt = record.createdAt,
          updatedAt = record.updatedAt
        )
      }
    }
  }

  override protected def convertToRecord: UserAccount => RIO[UserAccountRecord] = { aggregate =>
    ReaderT { _ =>
      Task.pure {
        UserAccountRecord(
          id = aggregate.id.value,
          status = aggregate.status.entryName,
          email = aggregate.emailAddress.value,
          password = aggregate.password.value,
          firstName = aggregate.firstName,
          lastName = aggregate.lastName,
          createdAt = aggregate.createdAt,
          updatedAt = aggregate.updatedAt
        )
      }
    }
  }

}
