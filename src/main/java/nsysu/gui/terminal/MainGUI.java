package nsysu.gui.terminal;

import nsysu.bank.HistoryRecord;
import nsysu.bank.account.BasicAccount;
import nsysu.bank.account.ExternalTransferable;
import nsysu.bank.account.Transactable;
import nsysu.bank.role.Administrator;
import nsysu.bank.role.Person;
import nsysu.bank.role.User;
import nsysu.util.enumtype.AccountType;
import nsysu.util.exception.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainGUI {
    static Scanner scanner= new Scanner(System.in);
    static Person person;
    static int action;
    public static <T> void showOperableAccount(String desc,User user,Class<T> CC){
        System.out.println(desc);
        ArrayList<BasicAccount> list = user.getAccount();
        for(int i=0;i<list.size();i++){
            BasicAccount account = list.get(i);
            if(CC.isInstance(account)){
                System.out.println(i+". "+account);
            }
        }
    }
    public static void main(String[] args) {
        while (true){
            System.out.print("UserName: ");
            String name = scanner.next();
            System.out.print("Password: ");
            String password = scanner.next();
            try{
                person = Person.logIn(name,password);
                break;
            }
            catch (TargetNotFindException e){
                System.out.print("UserName or Password error");
            }
            catch (ClosedUserException e){
                System.out.print("This user has been closed");
            }
        }
        while (true){
            System.out.println("You are "+person.getRole());
            System.out.println("You want to: (select number)");
            if(person instanceof Administrator administrator){
                System.out.println("1. add new user");
                System.out.println("2. deactivate user");
                System.out.println("3. give user money");
                System.out.println("4. unfreeze account");
                System.out.println("5. search user's account");
                System.out.println("6. refresh");
                System.out.println("0. exit");
                action = scanner.nextInt();
                if (action==0){
                    System.out.println("Exit");
                    break;
                }
                switch (action) {
                    case 1 -> {
                        while (true) {
                            System.out.print("UserName: ");
                            String name = scanner.next();
                            System.out.print("Password: ");
                            String password = scanner.next();
                            try {
                                System.out.println("Success to create new user: " + administrator.addNewUser(name, password));
                                break;
                            } catch (SameUserNameException e) {
                                System.out.println("The user have existed");
                            }
                        }
                    }
                    case 2 -> {
                        while (true) {
                            System.out.print("UserName: ");
                            String name = scanner.next();
                            try {
                                if (administrator.deactivateUser(name)) {
                                    System.out.println("Success to deactivate " + name);
                                } else {
                                    System.out.println("Fail to deactivate " + name);
                                }
                                break;
                            } catch (TargetNotFindException e) {
                                System.out.println("User name not find");
                            }
                        }
                    }
                    case 3 -> {
                        while (true) {
                            System.out.print("accountId: ");
                            String accountId = scanner.next();
                            System.out.print("amount: ");
                            double amount = scanner.nextDouble();
                            try {
                                if (administrator.giveMoney(accountId, amount)) {
                                    System.out.println("Success to give " + accountId + " " + amount);
                                    break;
                                } else {
                                    System.out.println("amount can't be negative");
                                }
                            } catch (IdNotFindException e) {
                                System.out.println("account not find");
                            }
                        }
                    }
                    case 4 -> {
                        while (true) {
                            System.out.print("accountId: ");
                            String accountId = scanner.next();
                            try {
                                if (administrator.unfreezeAccount(accountId)) {
                                    System.out.println("Success to unfreeze " + accountId);
                                } else {
                                    System.out.println("error! the account isn't frozen");
                                }
                                break;
                            } catch (TargetNotFindException e) {
                                System.out.println("User name not find");
                            }
                        }
                    }
                    case 5 -> {
                        while (true) {
                            System.out.print("userName: ");
                            String name = scanner.next();
                            try {
                                List<BasicAccount> accounts = administrator.getAccountByUserName(name);
                                System.out.println("number of account: " + accounts.size());
                                for (BasicAccount account : accounts) {
                                    System.out.println(account.toString());
                                }
                                break;
                            } catch (TargetNotFindException e) {
                                System.out.println("User name not find");
                            }
                        }
                    }
                    case 6 -> {
                        administrator.refresh();
                        System.out.println("Success to refresh");
                    }
                    default -> System.out.println("I don't know what you want");
                }
            }
            else if(person instanceof User user){
                System.out.println("1. add new account");
                System.out.println("2. query account");
                System.out.println("3. query total balance");
                System.out.println("4. transfer to your account");
                System.out.println("5. transfer to other's account");
                System.out.println("6. withdraw/deposit");
                System.out.println("7. close account");
                System.out.println("8. query history record");
                System.out.println("9. refresh");
                System.out.println("0. exit");
                action = scanner.nextInt();
                if (action==0){
                    System.out.println("Exit");
                    break;
                }
                switch (action) {
                    case 1 -> {
                        System.out.println("What kind of account do you want to create?");
                        System.out.println("1. " + AccountType.SavingsAccount.getStr());
                        System.out.println("2. " + AccountType.TimeDeposit.getStr());
                        System.out.println("3. " + AccountType.CheckingAccount.getStr());
                        System.out.println("4. " + AccountType.USDAccount.getStr());
                        int type = scanner.nextInt();
                        String result = switch (type) {
                            case 1 -> user.addAccount(AccountType.SavingsAccount);
                            case 2 -> user.addAccount(AccountType.TimeDeposit);
                            case 3 -> user.addAccount(AccountType.CheckingAccount);
                            case 4 -> user.addAccount(AccountType.USDAccount);
                            default -> null;
                        };
                        if(result!=null){
                            System.out.println("Success to create account No."+result);
                        }
                        else{
                            System.out.println("Fail to create account");
                        }
                    }
                    case 2 -> showOperableAccount("All account:", user, BasicAccount.class);
                    case 3 -> System.out.println("Total balance = " + user.queryTotalBalance());
                    case 4 -> {
                        while (true) {
                            showOperableAccount("Select your account to transfer out (number)", user, BasicAccount.class);
                            int from = scanner.nextInt();
                            showOperableAccount("Select your account to transfer in (number)", user, BasicAccount.class);
                            int to = scanner.nextInt();
                            System.out.print("amount:");
                            double amount = scanner.nextDouble();
                            System.out.print("description:");
                            String description = scanner.next();
                            try {
                                if (user.internalTransfer(from, to, amount, description)) {
                                    System.out.format("Success to transfer %.3f from %s to %s\n"
                                            , amount, user.getAccount().get(from).getId(), user.getAccount().get(to).getId());
                                    break;
                                } else {
                                    System.out.format("Fail to transfer %.3f from %s to %s. Please check account status, amount>0\n"
                                            , amount, user.getAccount().get(from).getId(), user.getAccount().get(to).getId());
                                }
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("IndexOutOfBounds");
                            } catch (NegativeBalanceException e) {
                                System.out.println("Account " + user.getAccount().get(from).getId()
                                        + " doesn't have enough money");
                            }
                        }
                    }
                    case 5 -> {
                        while (true) {
                            showOperableAccount("Select your account to transfer out (number)", user, ExternalTransferable.class);
                            int from = scanner.nextInt();
                            System.out.print("Select other's account to transfer in (id)");
                            String to = scanner.next();
                            System.out.print("amount:");
                            double amount = scanner.nextDouble();
                            System.out.print("description:");
                            String description = scanner.next();
                            try {
                                if (user.externalTransfer(from, to, amount, description)) {
                                    System.out.format("Success to transfer %.3f from %s to %s\n"
                                            , amount, user.getAccount().get(from).getId(), to);
                                    break;
                                } else {
                                    System.out.format("Fail to transfer %.3f from %s to %s. Please check account status, amount>0\n"
                                            , amount, user.getAccount().get(from).getId(), to);
                                }
                            } catch (IdNotFindException e) {
                                System.out.println(to + " not found");
                            } catch (IndexOutOfBoundsException e) {
                                System.out.println("IndexOutOfBounds");
                            } catch (NegativeBalanceException e) {
                                System.out.println("Account " + user.getAccount().get(from).getId()
                                        + " doesn't have enough money");
                            }
                        }
                    }
                    case 6 -> {
                        while (true) {
                            showOperableAccount("Select your account to withdraw or deposit (number)", user, Transactable.class);
                            int from = scanner.nextInt();
                            System.out.print("amount:");
                            double amount = scanner.nextDouble();
                            System.out.println("select withdraw or deposit (1 or 2)");
                            int action2 = scanner.nextInt();
                            if (action2 == 1) {
                                try {
                                    if (user.withdraw(from, amount)) {
                                        System.out.format("Success to withdraw %.3f from %s\n"
                                                , amount, user.getAccount().get(from).getId());
                                        break;
                                    } else {
                                        System.out.format("Fail to withdraw %.3f from %s. Please check account status, amount>0\n"
                                                , amount, user.getAccount().get(from).getId());
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    System.out.println("IndexOutOfBounds");
                                } catch (NegativeBalanceException e) {
                                    System.out.println("Account " + user.getAccount().get(from).getId()
                                            + " doesn't have enough money");
                                }
                            } else {
                                try {
                                    if (user.deposit(from, amount)) {
                                        System.out.format("Success to deposit %.3f to %s\n"
                                                , amount, user.getAccount().get(from).getId());
                                        break;
                                    } else {
                                        System.out.format("Fail to deposit %.3f to %s. Please check account status, amount>0\n"
                                                , amount, user.getAccount().get(from).getId());
                                    }
                                } catch (IndexOutOfBoundsException e) {
                                    System.out.println("IndexOutOfBounds");
                                }
                            }
                        }
                    }
                    case 7 -> {
                        showOperableAccount("Select your account to close (number)", user, BasicAccount.class);
                        int from = scanner.nextInt();
                        try {
                            if(user.closeAccount(from)){
                                System.out.println("Success to close account "+user.getAccount().get(from).getId());
                            }
                            else{
                                System.out.println("account "+user.getAccount().get(from).getId()+" has been closed state");
                            }
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("IndexOutOfBounds");
                        }
                    }
                    case 8 -> {
                        showOperableAccount("Select your account to query (number)", user, BasicAccount.class);
                        int from = scanner.nextInt();
                        try {
                            for(HistoryRecord history:user.getAccountHistory(from)){
                                System.out.println(history);
                            }
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("IndexOutOfBounds");
                        }
                    }
                    case 9 -> {
                        user.refresh();
                        System.out.println("Success to refresh");
                    }
                    default -> System.out.println("I don't know what you want");
                }
            }
        }
    }
}
