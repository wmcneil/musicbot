/*
 * MIT License
 *
 * Copyright (c) 2017 Frederik Ar. Mikkelsen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package fredboat.command.admin;

import fredboat.commandmeta.abs.Command;
import fredboat.commandmeta.abs.ICommandOwnerRestricted;
import fredboat.db.DatabaseManager;
import fredboat.util.TextUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class TestCommand extends Command implements ICommandOwnerRestricted {

    @Override
    public void onInvoke(Guild guild, TextChannel channel, Member invoker, Message message, String[] args) {
        TextUtils.replyWithName(channel, invoker, "Beginning stress test");

        for (int i = 0; i < 20; i++) {
            new StressTestThread().start();
        }
    }

    private class StressTestThread extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 2000; i++) {
                DatabaseManager.getEntityManager().getTransaction().begin();

                DatabaseManager.getEntityManager()
                        .createNativeQuery("INSERT INTO test VALUES (:val);")
                        .setParameter("val", (int)(Math.random() * 10000))
                        .executeUpdate();

                DatabaseManager.getEntityManager().getTransaction().commit();
            }
        }
    }
}
